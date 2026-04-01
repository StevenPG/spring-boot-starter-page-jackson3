package com.stevenpg.data.jackson3.page;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;

/**
 * Auto-configuration that registers a Jackson 3 mixin so that
 * {@link Page} can be deserialized from JSON (e.g. when consuming
 * paginated REST responses via {@code RestClient} or {@code HttpServiceProxyFactory}).
 *
 * <p>Spring Data's built-in {@code PageModule} handles <em>serialization</em> only.
 * This auto-configuration fills the deserialization gap by mapping the {@link Page}
 * interface to {@link RestPage} via a {@code @JsonDeserialize} mixin.</p>
 *
 * <p>Activates automatically when both {@link Page} (spring-data-commons) and
 * {@link JsonMapperBuilderCustomizer} (spring-boot-jackson) are on the classpath.</p>
 */
@AutoConfiguration
@ConditionalOnClass({Page.class, JsonMapperBuilderCustomizer.class})
public class PageJackson3AutoConfiguration {

    /**
     * Constructor for Spring.
     */
    public PageJackson3AutoConfiguration() {
        // No-op constructor for Spring
    }

    @Bean
    JsonMapperBuilderCustomizer pageDeserializationCustomizer() {
        return builder -> builder.addMixIn(Page.class, PageDeserializeMixin.class);
    }
}
