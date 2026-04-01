package com.stevenpg.data.jackson3.page;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import tools.jackson.databind.annotation.JsonDeserialize;

/**
 * Jackson 3 mixin applied to {@link org.springframework.data.domain.Page} that
 * directs Jackson to deserialize the interface as {@link RestPage}.
 */
@JsonDeserialize(as = RestPage.class)
@JsonIgnoreProperties(ignoreUnknown = true)
interface PageDeserializeMixin {}
