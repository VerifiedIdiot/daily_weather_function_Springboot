package com.highlight.weather.currentAndDaily.service;

import com.highlight.weather.currentAndDaily.dto.CurrentWeatherApiDto;
import com.highlight.weather.currentAndDaily.dto.CurrentWeatherResponseDto;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CurrentWeatherService {

    @Value("${api.currentWeather.url}")
    private String currentWeatherUrl;

    @Value("${api.currentWeather.apiKey}")
    private String currentWeatherApiKey;

    @Autowired
    private final RestClient restClient;

    private static final Logger logger = LogManager.getLogger(CurrentWeatherService.class);

    public ResponseEntity<?> getCurrentWeather(String x, String y) {
        try {
            Map<String, String> dateTimeParams = dateTimeMethod();
            return sendGetRequest(x, y, dateTimeParams);
        } catch (Exception e) {
            logger.error("Service error: " + e.getMessage(), e);
        }
        return null;
    }

    // 인자가 너무 많은경우 Map<K,V>로 받을 수 있지만 같은 취준생들에게 공유하기 위해서 다 변수로 받음
    // 가독성이 중요하면 아래와 같이 바꿔주세요 여러분
    //     UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url);
    //    queryParams.forEach(builder::queryParam);
    // 만약 인자들이 처음부터 끝까지 사용하기로 정해져있고 key를 알필요 없으면 list가 순회하는게 더 빠르니 이 경우 List를 사용하자
    private ResponseEntity<?> sendGetRequest(String x, String y, Map<String, String> dateTimeParams) {
        String uri = UriComponentsBuilder.fromHttpUrl(currentWeatherUrl)
                .queryParam("ServiceKey", currentWeatherApiKey)
                .queryParam("nx", x)
                .queryParam("ny", y)
                .queryParam("base_date", dateTimeParams.get("baseDate"))
                .queryParam("base_time", dateTimeParams.get("baseTime"))
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 10)
                .queryParam("dataType", "JSON")
                // 자동 인코딩 방지하여 쿼리파마리터의 값 그대로 url 구성
                .build()
                .toString();
        try {
            System.out.println(uri);
            CurrentWeatherApiDto currentWeatherApiDto = restClient.get()
                    .uri(uri)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(CurrentWeatherApiDto.class)
                    .getBody();
            Map<String, String> weatherData = parseWeatherData(currentWeatherApiDto);
            CurrentWeatherResponseDto currentWeatherResponseDto = ToDtoFromMap(weatherData);
            return ResponseEntity.ok(currentWeatherResponseDto);
        } catch (UnknownContentTypeException e) {
            ResponseEntity<?> response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .toEntity(String.class);
            return response;
        } catch (RestClientException e) {
            throw new RuntimeException("RestClient 에러 : " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("알 수 없는 에러가 발생: ", e);
        }
    }

    private Map<String, String> dateTimeMethod() {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDateTime tracedTime = now.getMinute() < 30 ? now.minusMinutes(30) : now;
        String time = tracedTime.format(DateTimeFormatter.ofPattern("HHmm"));

        Map<String, String> dateTimeParams = new HashMap<>();
        dateTimeParams.put("baseDate", date);
        dateTimeParams.put("baseTime", time);

        return dateTimeParams;
    }

    private Map<String, String> parseWeatherData(CurrentWeatherApiDto currentWeatherApiDto) {
        try {
            Map<String, String> weatherData = new HashMap<>();
            currentWeatherApiDto.getResponse().getBody().getItems().getItem().forEach(item -> {
                String category = item.getCategory();
                String obsrValue = item.getObsrValue();

                switch (category) {
                    case "T1H":
                        weatherData.put("temperature", obsrValue + "°");
                        break;
                    case "REH":
                        weatherData.put("humidity", obsrValue + "%");
                        break;
                    case "RN1":
                        weatherData.put("rain", obsrValue + "mm");
                        break;
                    case "PTY":
                        Map<String, String> conditions = Map.of(
                                "0", "맑음", "1", "비", "2", "비/눈", "3", "눈",
                                "5", "빗방울", "6", "빗방울 눈날림", "7", "눈날림");
                        weatherData.put("condition", conditions.getOrDefault(obsrValue, obsrValue));
                        break;
                    case "WSD":
                        weatherData.put("wind", obsrValue + "m/s");
                        break;
                }
            });
            return weatherData;
        } catch (Exception e) {
            logger.error("Error parsing JSON response: " + e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    //builder
    private static CurrentWeatherResponseDto ToDtoFromMap(Map<String, String> weatherData) {
        return CurrentWeatherResponseDto.builder()
                .condition(weatherData.get("condition"))
                .humidity(weatherData.get("humidity"))
                .rain(weatherData.get("rain"))
                .temperature(weatherData.get("temperature"))
                .wind(weatherData.get("wind"))
                .build();
    }
}