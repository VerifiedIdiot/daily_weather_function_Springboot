package com.highlight.weather.currentAndDaily.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class HourlyWeatherApiDto {
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
        @JsonProperty("fcstDate")
        private String fcstDate;
        @JsonProperty("fcstTime")
        private String fcstTime;
        @JsonProperty("fcstValue")
        private String fcstValue;


    }
}