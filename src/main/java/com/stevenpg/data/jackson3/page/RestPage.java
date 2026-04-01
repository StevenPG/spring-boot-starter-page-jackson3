package com.stevenpg.data.jackson3.page;

import java.io.Serial;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Jackson 3–friendly subclass of {@link PageImpl} that supports deserialization
 * from JSON responses produced by Spring Data REST endpoints.
 *
 * <p>Spring Data's {@link PageImpl} lacks a {@code @JsonCreator} constructor,
 * so Jackson cannot deserialize JSON directly into it. This class bridges the gap
 * by providing a constructor annotated with {@code @JsonCreator} that maps the
 * standard page JSON fields ({@code content}, {@code number}, {@code size},
 * {@code totalElements}) to a valid {@link PageImpl} instance.</p>
 *
 * <p>All fields use wrapper types to safely handle {@code null} values, which
 * occur in unpaged responses.</p>
 *
 * @param <T> the element type of the page content
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestPage<T> extends PageImpl<T> {

    private static final @Serial long serialVersionUID = 1L;

    /**
     * Constructor for deserialization.
     * @param content the page content
     * @param number the page number (zero-based)
     * @param size the page size
     * @param totalElements the total number of elements
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPage(
            @JsonProperty("content") List<T> content,
            @JsonProperty("number") Integer number,
            @JsonProperty("size") Integer size,
            @JsonProperty("totalElements") Long totalElements) {
        super(content != null ? content : List.of(),
                PageRequest.of(
                        number != null ? number : 0,
                        size != null && size > 0 ? size : 1),
                totalElements != null ? totalElements : 0L);
    }
}
