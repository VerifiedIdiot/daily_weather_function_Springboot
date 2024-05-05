package com.highlight.weather.currentAndDaily.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentWeatherResponseDto {

    private String condition;

    private String humidity;

    private String rain;

    private String temperature;

    private String wind;
}
