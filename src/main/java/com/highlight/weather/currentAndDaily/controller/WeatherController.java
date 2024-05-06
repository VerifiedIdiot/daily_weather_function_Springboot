package com.highlight.weather.currentAndDaily.controller;

import com.highlight.weather.currentAndDaily.dto.CurrentWeatherResponseDto;
import com.highlight.weather.currentAndDaily.service.CurrentWeatherService;
import com.highlight.weather.currentAndDaily.service.HourlyWeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.UnknownContentTypeException;

import java.io.IOException;
import java.util.logging.Logger;


@Log4j2
@Tag(name = "날씨_refactored", description = "날씨 관련 API_refactored")
@RestController
@RequiredArgsConstructor
@RequestMapping("/weather-refactored")
public class WeatherController {

    private final CurrentWeatherService currentWeatherService;
    // DB에서 AXIOS API 요청에 맞춰 각 지역별 일주일 치 날씨 정보 형태로 가공해서 return하는 컨트롤러
    private final HourlyWeatherService hourlyWeatherService;

    @GetMapping("/get-current")
    @Operation(summary = "날씨정보 조회", description = "사용자 위치 기반 실시간 날씨 정보를 조회합니다.")
    // API 서버의 에러 응답 형식이 JSON이 아닌 XML로 들어오기에 추가 처리를 해야함
    public ResponseEntity<?> getCurrentWeather(@RequestParam("x") String x, @RequestParam("y") String y) throws UnknownContentTypeException ,NullPointerException {
        try {
            return ResponseEntity.ok(currentWeatherService.getCurrentWeather(x, y).getBody());
        } catch (Exception e) {
            Logger.getLogger("컨트롤러 에러 발생" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/get-hourly")
    @Operation(summary = "날씨정보 조회", description = "사용자 위치 기반 한시간 단위 하루의 날씨 정보를 조회합니다.")
    // API 서버의 에러 응답 형식이 JSON이 아닌 XML로 들어오기에 추가 처리를 해야함
    public ResponseEntity<?> getHourlyWeather(@RequestParam("x") String x, @RequestParam("y") String y) throws UnknownContentTypeException, NullPointerException {
        try {
            return ResponseEntity.ok(hourlyWeatherService.getHourlyWeather(x, y).getBody());
        } catch (Exception e) {
            Logger.getLogger("컨트롤러 에러 발생" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

    }


}
