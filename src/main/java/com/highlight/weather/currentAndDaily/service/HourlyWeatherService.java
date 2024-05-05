package com.highlight.weather.currentAndDaily.service;

import com.highlight.weather.currentAndDaily.dto.HourlyWeatherApiDto;
import com.highlight.weather.currentAndDaily.dto.HourlyWeatherResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.UnknownContentTypeException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HourlyWeatherService {
    @Value("${api.hourlyWeather.url}")
    private String hourlyWeatherUrl;

    @Value("${api.hourlyWeather.apiKey}")
    private String hourlyWeatherApiKey;

    @Autowired
    private final RestClient restClient;

    private static final Logger logger = LogManager.getLogger(CurrentWeatherService.class);

    public ResponseEntity<?> getHourlyWeather(String x, String y) {
        try {
            System.out.println(hourlyWeatherApiKey);
            Map<String, String> dateTimeParams = dateTimeMethod();
            return sendGetRequest(x, y, dateTimeParams);
        } catch (Exception e) {
            logger.error("Service error: " + e.getMessage(), e);
            throw new RuntimeException("public 서비스 메서드 실패 : " + e.getMessage(), e);
        }


    }

    private ResponseEntity<?> sendGetRequest(String x, String y, Map<String, String> dateTimeParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(hourlyWeatherUrl)
                .queryParam("ServiceKey", hourlyWeatherApiKey)
                .queryParam("nx", x)
                .queryParam("ny", y)
                .queryParam("base_date", dateTimeParams.get("baseDate"))
                .queryParam("base_time", dateTimeParams.get("baseTime"))
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 301)
                .queryParam("dataType", "JSON");


        try {
            HourlyWeatherApiDto hourlyWeatherApiDto = restClient.get()
                    .uri(builder.toUriString())
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(HourlyWeatherApiDto.class)
                    .getBody();

            System.out.println(hourlyWeatherApiDto.getResponse());
            Map<String, HourlyWeatherResponseDto.WeatherDetail> weatherData = parseWeatherData(hourlyWeatherApiDto);
            return ResponseEntity.ok(weatherData);
        } catch (UnknownContentTypeException e) {
            ResponseEntity<?> response = restClient.get()
                    .uri(builder.toUriString())
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(String.class);
            return response;
        } catch (RestClientException e) {
            throw new RuntimeException("API 요청에 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("알 수 없는 에러가 발생: ", e);
        }
    }

    private Map<String, String> dateTimeMethod() {
        LocalDateTime now = LocalDateTime.now();
        // 담당자가 API를 업데이트하는 시간
        int[] forecastTimes = {2, 5, 8, 11, 14, 17, 20, 23};
        int latestForecastTime = 0;

        // 현재 시간보다 이전인 가장 가까운 업데이트 시간 찾기
        for (int hour : forecastTimes) {
            if (hour <= now.getHour()) {
                latestForecastTime = hour;
            }
        }
        LocalDateTime forecastDateTime = now.withHour(latestForecastTime);
        String date = forecastDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time = forecastDateTime.format(DateTimeFormatter.ofPattern("HH00"));
        Map<String, String> dateTimeParams = new HashMap<>();
        dateTimeParams.put("baseDate", date);
        dateTimeParams.put("baseTime", time);
        return dateTimeParams;
    }


    private TreeMap<String, HourlyWeatherResponseDto.WeatherDetail> parseWeatherData(HourlyWeatherApiDto hourlyWeatherApiDto) {
        TreeMap<String, HourlyWeatherResponseDto.WeatherDetail> detailsMap = new TreeMap<>();

        try {
            hourlyWeatherApiDto.getResponse().getBody().getItems().getItem().forEach(item -> {
                String date = item.getFcstDate();
                String time = item.getFcstTime();
                String dateTime = item.getFcstDate() + item.getFcstTime();
                HourlyWeatherResponseDto.WeatherDetail detail = detailsMap.getOrDefault(dateTime, new HourlyWeatherResponseDto.WeatherDetail());

                String category = item.getCategory();
                String value = item.getFcstValue();

                switch (category) {
                    case "TMP":
                        detail.setTemperature(value + "°");
                        break;
                    case "WSD":
                        detail.setWindSpeed(value + "m/s");
                        break;
                    case "SKY":
                        Map<String, String> skies = Map.of("1", "맑음", "3", "구름많음", "4", "흐림");
                        detail.setSky(skies.getOrDefault(value, "Unknown"));
                        break;
                    case "REH":
                        detail.setHumidity(value + "%");
                        break;
                    case "POP":
                        detail.setRainChance(value + "%");
                        break;
                    case "PCP":
                        detail.setWeatherCondition(value);
                        break;
                    case "PTY":
                        detail.setRainAmount(value +"mm");
                        break;


                }

                detailsMap.put(dateTime, detail);
            });
        } catch (Exception e) {
            logger.error("DTO 파싱 중 에러 발생: " + e.getMessage(), e);
            return new TreeMap<>();
        }

        return detailsMap;
    }
}
