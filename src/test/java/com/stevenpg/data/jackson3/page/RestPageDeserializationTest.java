package com.stevenpg.data.jackson3.page;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

class RestPageDeserializationTest {

    private JsonMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = JsonMapper.builder()
                .addMixIn(Page.class, PageDeserializeMixin.class)
                .build();
    }

    @Test
    void deserializesStandardPageJson() {
        String json = """
                {
                  "content": [{"name": "Alice"}, {"name": "Bob"}],
                  "number": 0,
                  "size": 20,
                  "totalElements": 100,
                  "totalPages": 5,
                  "first": true,
                  "last": false,
                  "numberOfElements": 2,
                  "empty": false
                }
                """;

        Page<?> page = mapper.readValue(json, Page.class);

        assertEquals(2, page.getContent().size());
        assertEquals(0, page.getNumber());
        assertEquals(20, page.getSize());
        assertEquals(100, page.getTotalElements());
        assertInstanceOf(RestPage.class, page);
    }

    @Test
    void handlesNullPageMetadata() {
        String json = """
                {
                  "content": [{"id": 1}],
                  "number": null,
                  "size": null,
                  "totalElements": null
                }
                """;

        Page<?> page = mapper.readValue(json, Page.class);

        assertEquals(1, page.getContent().size());
        assertEquals(0, page.getNumber());
        assertEquals(1, page.getSize());
        assertEquals(1, page.getTotalElements()); // PageImpl infers total from content size when totalElements is null
    }

    @Test
    void handlesMissingPageMetadata() {
        String json = """
                {
                  "content": [{"id": 1}]
                }
                """;

        Page<?> page = mapper.readValue(json, Page.class);

        assertEquals(1, page.getContent().size());
        assertEquals(0, page.getNumber());
    }

    @Test
    void handlesEmptyContent() {
        String json = """
                {
                  "content": [],
                  "number": 0,
                  "size": 10,
                  "totalElements": 0
                }
                """;

        Page<?> page = mapper.readValue(json, Page.class);

        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());
        assertEquals(0, page.getTotalPages());
    }

    @Test
    void handlesNullContent() {
        String json = """
                {
                  "content": null,
                  "number": 0,
                  "size": 10,
                  "totalElements": 0
                }
                """;

        Page<?> page = mapper.readValue(json, Page.class);

        assertTrue(page.getContent().isEmpty());
    }

    @Test
    void ignoresUnknownFields() {
        String json = """
                {
                  "content": [],
                  "number": 0,
                  "size": 10,
                  "totalElements": 0,
                  "pageable": {"sort": {"sorted": false}},
                  "sort": {"sorted": false, "unsorted": true},
                  "customField": "should be ignored"
                }
                """;

        assertDoesNotThrow(() -> mapper.readValue(json, Page.class));
    }

    @Test
    void preservesPaginationCalculations() {
        String json = """
                {
                  "content": ["a", "b", "c"],
                  "number": 2,
                  "size": 3,
                  "totalElements": 10
                }
                """;

        Page<?> page = mapper.readValue(json, Page.class);

        assertEquals(2, page.getNumber());
        assertEquals(3, page.getSize());
        assertEquals(10, page.getTotalElements());
        assertEquals(4, page.getTotalPages());
        assertTrue(page.hasNext());
        assertTrue(page.hasPrevious());
    }

    @Test
    void handlesZeroSizeGracefully() {
        String json = """
                {
                  "content": [],
                  "number": 0,
                  "size": 0,
                  "totalElements": 0
                }
                """;

        Page<?> page = mapper.readValue(json, Page.class);

        // size=0 should default to 1 to avoid ArithmeticException in PageImpl
        assertEquals(1, page.getSize());
    }
}
