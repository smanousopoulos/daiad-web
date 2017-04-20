package eu.daiad.web.service.message.resolvers;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.daiad.web.annotate.message.MessageGenerator;
import eu.daiad.web.model.EnumTimeAggregation;
import eu.daiad.web.model.EnumTimeUnit;
import eu.daiad.web.model.device.EnumDeviceType;
import eu.daiad.web.model.message.EnumRecommendationTemplate;
import eu.daiad.web.model.message.Message;
import eu.daiad.web.model.message.MessageResolutionStatus;
import eu.daiad.web.model.message.Recommendation.ParameterizedTemplate;
import eu.daiad.web.model.message.ScoringMessageResolutionStatus;
import eu.daiad.web.model.message.SimpleMessageResolutionStatus;
import eu.daiad.web.model.query.DataQuery;
import eu.daiad.web.model.query.DataQueryBuilder;
import eu.daiad.web.model.query.DataQueryResponse;
import eu.daiad.web.model.query.EnumDataField;
import eu.daiad.web.model.query.EnumMetric;
import eu.daiad.web.model.query.EnumMeasurementDataSource;
import eu.daiad.web.model.query.SeriesFacade;
import eu.daiad.web.service.ICurrencyRateService;
import eu.daiad.web.service.IDataService;
import eu.daiad.web.service.message.AbstractRecommendationResolver;

import static eu.daiad.web.model.query.Point.betweenTime;

@MessageGenerator(period = "P1D")
@Component
@Scope("prototype")
public class InsightA2DailyConsumption extends AbstractRecommendationResolver
{
    public static class Parameters extends Message.AbstractParameters
        implements ParameterizedTemplate
    {
        /** A minimum value for daily volume consumption */
        private static final String MIN_VALUE = "1E+0"; 

        @NotNull
        @DecimalMin(MIN_VALUE)
        private Double currentValue;

        @NotNull
        @DecimalMin(MIN_VALUE)
        private Double averageValue;

        public Parameters()
        {
            super();
        }

        public Parameters(
            DateTime refDate, EnumDeviceType deviceType, double currentValue, double averageValue)
        {
            super(refDate, deviceType);
            this.averageValue = averageValue;
            this.currentValue = currentValue;
        }

        @JsonProperty("currentValue")
        public void setCurrentValue(double y)
        {
            this.currentValue = y;
        }

        @JsonProperty("currentValue")
        public Double getCurrentValue()
        {
            return currentValue;
        }

        @JsonProperty("averageValue")
        public void setAverageValue(double y)
        {
            this.averageValue = y;
        }

        @JsonProperty("averageValue")
        public Double getAverageValue()
        {
            return averageValue;
        }

        @JsonIgnore
        @Override
        public Map<String, Object> getParameters()
        {
            Map<String, Object> parameters = super.getParameters();

            parameters.put("value", currentValue);
            parameters.put("consumption", currentValue);     

            parameters.put("average_value", averageValue);
            parameters.put("average_consumption", averageValue);

            Double percentChange = 100.0 * Math.abs(((currentValue - averageValue) / averageValue));
            parameters.put("percent_change", Integer.valueOf(percentChange.intValue()));

            return parameters;
        }

        @JsonIgnore
        @Override
        public EnumRecommendationTemplate getTemplate()
        {
            boolean incr = (averageValue <= currentValue);
            if (deviceType == EnumDeviceType.AMPHIRO)
                return incr?
                    EnumRecommendationTemplate.INSIGHT_A2_SHOWER_DAILY_CONSUMPTION_INCR:
                    EnumRecommendationTemplate.INSIGHT_A2_SHOWER_DAILY_CONSUMPTION_DECR;
            else
                return incr?
                    EnumRecommendationTemplate.INSIGHT_A2_METER_DAILY_CONSUMPTION_INCR:
                    EnumRecommendationTemplate.INSIGHT_A2_METER_DAILY_CONSUMPTION_DECR;
        }

        @Override
        public Parameters withLocale(Locale target, ICurrencyRateService currencyRate)
        {
            return this;
        }
    }
    
    @Autowired
    IDataService dataService;
    
    @Override
    public List<MessageResolutionStatus<ParameterizedTemplate>> resolve(
        UUID accountKey, EnumDeviceType deviceType)
    {
        final double K = 1.50;  // a threshold (z-score) of significant change
        final int N = 40;       // number of past days to examine
        final double F = 0.6;   // a threshold ratio of non-nulls for collected values
        final double dailyThreshold = config.getVolumeThreshold(deviceType, EnumTimeUnit.DAY);
        
        // Build a common part of a data-service query

        DataQuery query;
        DataQueryResponse queryResponse;
        SeriesFacade series;
        Interval interval = null;

        DataQueryBuilder queryBuilder = new DataQueryBuilder()
            .timezone(refDate.getZone())
            .user("user", accountKey)
            .source(EnumMeasurementDataSource.fromDeviceType(deviceType))
            .sum();

        // Compute for target day
        
        DateTime start = refDate.withTimeAtStartOfDay();
        
        query = queryBuilder
            .sliding(start, +1, EnumTimeUnit.DAY, EnumTimeAggregation.DAY)
            .build();
        queryResponse = dataService.execute(query);
        series = queryResponse.getFacade(deviceType);
        interval = query.getTime().asInterval();
        Double targetValue = (series != null)? 
            series.get(EnumDataField.VOLUME, EnumMetric.SUM, betweenTime(interval)):
            null;
        if (targetValue == null || targetValue < dailyThreshold)
            return Collections.emptyList(); // nothing to compare to
        
        // Compute for past N days

        SummaryStatistics summary = new SummaryStatistics();
        for (int i = 0; i < N; i++) {
            start = start.minusDays(1);
            query = queryBuilder
                .sliding(start, +1, EnumTimeUnit.DAY, EnumTimeAggregation.DAY)
                .build();
            queryResponse = dataService.execute(query);
            series = queryResponse.getFacade(deviceType);
            interval = query.getTime().asInterval();
            Double val = (series != null)? 
                series.get(EnumDataField.VOLUME, EnumMetric.SUM, betweenTime(interval)):
                null;
            if (val != null)
                summary.addValue(val);
        }
        if (summary.getN() < N * F)
            return Collections.emptyList(); // too few values
        
        // Seems we have sufficient data for the past days

        double averageValue = summary.getMean();
        if (averageValue < dailyThreshold)
            return Collections.emptyList(); // not reliable; consumption is too low

        double sd = Math.sqrt(summary.getPopulationVariance());
        double normValue = (sd > 0)? ((targetValue - averageValue) / sd) : Double.POSITIVE_INFINITY;
        double score = (sd > 0)? (Math.abs(normValue) / (2 * K)) : Double.POSITIVE_INFINITY;

        debug(
            "%s/%s: Computed consumption for period P%dD to %s: " +
                "%.2f μ=%.2f σ=%.2f x*=%.2f score=%.2f",
             accountKey, deviceType, N, refDate.toString("dd/MM/YYYY"),
             targetValue, averageValue, sd, normValue, score);
        
        ParameterizedTemplate parameterizedTemplate = 
            new Parameters(refDate, deviceType, targetValue, averageValue);
        MessageResolutionStatus<ParameterizedTemplate> result = 
            new ScoringMessageResolutionStatus<>(score, parameterizedTemplate);
        return Collections.singletonList(result);
    }

}
