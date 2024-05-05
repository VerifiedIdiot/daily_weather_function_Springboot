package com.highlight.weather.currentAndDaily.dto;


import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HourlyWeatherResponseDto {

    private Map<String,WeatherDetail> forecastDateTime;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder

    public static class WeatherDetail {
        private String temperature;
        private String humidity;
        private String weatherCondition;
        private String sky;
        private String rainChance;
        private String rainAmount;
        private String windSpeed;
    }
}
