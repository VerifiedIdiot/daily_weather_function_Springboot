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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

            Map<String, String> dateTimeParams = dateTimeMethod();
            return sendGetRequest(x, y, dateTimeParams);
        } catch (Exception e) {
            logger.error("Service error: " + e.getMessage(), e);
            throw new RuntimeException("public 서비스 메서드 실패 : " + e.getMessage(), e);
        }


    }
    // 인자가 너무 많은경우 Map<K,V>로 받을 수 있지만 같은 취준생들에게 공유하기 위해서 다 변수로 받음
    // 가독성이 중요하면 아래와 같이 바꿔주세요 여러분
    //     UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url);
    //    queryParams.forEach(builder::queryParam);
    // 만약 인자들이 처음부터 끝까지 사용하기로 정해져있고 key를 알필요 없으면 list가 순회하는게 더 빠르니 이 경우 List를 사용하자
    private ResponseEntity<?> sendGetRequest(String x, String y, Map<String, String> dateTimeParams) {
        String uri = UriComponentsBuilder.fromHttpUrl(hourlyWeatherUrl)
                .queryParam("ServiceKey", hourlyWeatherApiKey)
                .queryParam("nx", x)
                .queryParam("ny", y)
                .queryParam("base_date", dateTimeParams.get("baseDate"))
                .queryParam("base_time", dateTimeParams.get("baseTime"))
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 301)
                .queryParam("dataType", "JSON")
                .build()
                .toString();


        try {
            HourlyWeatherApiDto hourlyWeatherApiDto = restClient.get()
                    .uri(uri)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(HourlyWeatherApiDto.class)
                    .getBody();

            Map<String, HourlyWeatherResponseDto.WeatherDetail> weatherData = parseWeatherData(hourlyWeatherApiDto);
            return ResponseEntity.ok(weatherData);
        } catch (UnknownContentTypeException e) {
            ResponseEntity<?> response = restClient.get()
                    .uri(uri)
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
        // 파이썬에서는 딕셔너리를 사용, 딕셔너리는 삽입 순서를 보장하고 HashMap은 보장하지않음, 그런 이유로 TreeMap으로 대체
        TreeMap<String, HourlyWeatherResponseDto.WeatherDetail> detailsMap = new TreeMap<>();

        try {
            hourlyWeatherApiDto.getResponse().getBody().getItems().getItem().forEach(item -> {
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
