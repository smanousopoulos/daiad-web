package eu.daiad.web.service.weather;

import java.util.List;

import org.joda.time.DateTime;

import eu.daiad.web.domain.application.WeatherServiceEntity;

/**
 * Provides methods for storing and querying weather services and data
 *
 */
public interface IWeatherRepository {

    /**
     * Returns all registered weather services.
     * 
     * @return A list of @{link WeatherServiceEntity} 
     */
    List<WeatherServiceEntity> getServices();

    /**
     * Returns meta data for the weather service with the given identifier.
     * 
     * @param serviceId The service identifier.
     * @return The service meta data.
     */
    WeatherServiceEntity getServiceById(int serviceId);

    /**
     * Returns meta data for the weather service with the given name.
     * 
     * @param serviceId The service name.
     * @return The service meta data.
     */
    WeatherServiceEntity getServiceByName(String serviceName);
    
    /**
     * Updates weather data for a specific utility as loaded from a specific weather data harvester.
     * 
     * @param Identifier of the weather service used for generating the data.
     * @param Identifier of the utility the data refer to.
     * @param Weather data generation time stamp from the data provider.
     * @param data The weather data.
     */
    void update(int serviceId, int utilityId, DateTime createdOn, List<DailyWeatherData> data);

    /**
     * Queries hourly weather data for a specific interval.
     * 
     * @param utilityId Utility identifier.
     * @param serviceId Weather service identifier.
     * @param from Start date.
     * @param to End date.
     * 
     * @return A list of @{link HourlyWeatherData}
     */
    List<HourlyWeatherData> getHourlyData(int serviceId, int utilityId, DateTime from, DateTime to);
    
    /**
     * Queries daily weather data for a specific interval.
     * 
     * @param utilityId Utility identifier.
     * @param serviceId Weather service identifier.
     * @param from Start date.
     * @param to End date.
     * 
     * @return A list of @{link DailyWeatherData}
     */
    List<DailyWeatherData> getDailyData(int serviceId, int utilityId, DateTime from, DateTime to);

}
