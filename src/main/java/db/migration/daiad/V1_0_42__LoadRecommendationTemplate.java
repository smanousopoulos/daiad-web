package db.migration.daiad;

import java.util.Arrays;

import org.apache.commons.collections4.FluentIterable;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang.StringUtils;
import org.flywaydb.core.api.migration.MigrationChecksumProvider;

import db.initialization.LoadRecommendationTemplate;
import eu.daiad.web.model.message.EnumRecommendationTemplate;
import eu.daiad.web.util.EnumToNameTransformer;

public class V1_0_42__LoadRecommendationTemplate extends LoadRecommendationTemplate
    implements MigrationChecksumProvider
{
    private static final Transformer<EnumRecommendationTemplate, String> TRANSFORM_TO_NAME =
        new EnumToNameTransformer<>();

    @Override
    public Integer getChecksum()
    {
        String[] names = FluentIterable.of(EnumRecommendationTemplate.values())
            .transform(TRANSFORM_TO_NAME)
            .toArray(String.class);

        Arrays.sort(names);
        return StringUtils.join(names, "$").hashCode();
    }
}
