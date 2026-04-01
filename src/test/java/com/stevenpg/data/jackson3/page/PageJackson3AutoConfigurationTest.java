package com.stevenpg.data.jackson3.page;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.domain.Page;

import tools.jackson.databind.json.JsonMapper;

import tools.jackson.databind.exc.InvalidDefinitionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageJackson3AutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    JacksonAutoConfiguration.class,
                    PageJackson3AutoConfiguration.class));

    @Test
    void autoConfigurationRegistersCustomizer() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(PageJackson3AutoConfiguration.class);
            assertThat(context).hasBean("pageDeserializationCustomizer");
        });
    }

    @Test
    void jsonMapperFailsToDeserializePageWithoutAutoConfiguration() {
        JsonMapper mapper = JsonMapper.builder().build();
        String json = """
                {
                  "content": [{"value": 42}],
                  "number": 0,
                  "size": 10,
                  "totalElements": 1
                }
                """;

        assertThatThrownBy(() -> mapper.readValue(json, Page.class))
                .isInstanceOf(InvalidDefinitionException.class);
    }

    @Test
    void jsonMapperCanDeserializePage() {
        contextRunner.run(context -> {
            JsonMapper mapper = context.getBean(JsonMapper.class);
            String json = """
                    {
                      "content": [{"value": 42}],
                      "number": 0,
                      "size": 10,
                      "totalElements": 1
                    }
                    """;

            Page<?> page = mapper.readValue(json, Page.class);

            assertThat(page.getContent()).hasSize(1);
            assertThat(page.getTotalElements()).isEqualTo(1);
            assertThat(page).isInstanceOf(RestPage.class);
        });
    }
}
