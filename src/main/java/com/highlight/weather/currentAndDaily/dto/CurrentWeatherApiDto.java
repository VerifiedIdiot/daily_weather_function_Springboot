package com.highlight.weather.currentAndDaily.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrentWeatherApiDto {
    @JsonProperty("response")
    private Response response;

    @Getter
    public static class Response {
        @JsonProperty("body")
        private Body body;
    }

    @Getter
    public static class Body {

        @JsonProperty("items")
        private Items items;
    }

    @Getter
    public static class Items {
        @JsonProperty("item")
        private List<Item> item;
    }

    @Getter
    public static class Item {
        @JsonProperty("category")
        private String category;
        @JsonProperty("obsrValue")
        private String obsrValue;
    }
}
