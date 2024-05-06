# 현재위치 실시간 & 하루단위 시간별 날씨정보 조회 기능 </br> (플라스크 -> 스프링부트)
- 사이드 프로젝트에서 플라스크에서 구현했던 기능을 스프링부트로 이식
- 실시간 데이터 활용에 맞춘 성능개선
- 불필요한 라이브러리 및 코드 제거
 
## 사용 스택
- Java
- Spring Boot
- Swagger

## 스웨거 주소
- Swagger: (http://localhost:8080/swagger-ui/index.html#)

## 사전 준비 사항 및 주의사항
- API KEY 발급([https://apihub.kma.go.kr/](https://www.data.go.kr/iim/api/selectAPIAcountView.do)) 발급된 API키를 사용할때는 각 방식에(인코딩 or 디코딩) 따라서 세심한 주의가 필요하다 </br>
  나의 경우 UriComponentsBuilder를 사용하여 요청 파라미터들을 인코딩 처리를 하는데, 이 경우 "+"를 공백으로 처리하여 이 부분에 한해서 추가적인 처리가 필요하였음
  </br>(참고 : (https://velog.io/@naked_thunder/RestTemplate-API%EC%97%B0%EB%8F%99%ED%95%98%EA%B8%B0), https://velog.io/@yeahg_dev/%EA%B3%B5%EA%B3%B5%EB%8D%B0%EC%9D%B4%ED%84%B0%ED%8F%AC%ED%84%B8-SERVICEKEYISNOTREGISTEREDERROR-%EC%9B%90%EC%9D%B8-%ED%8C%8C%ED%97%A4%EC%B9%98%EA%B8%B0 )
- content-type을 JSON으로 설정을 해도 옳바른 결과값을 제외한 상태 메시지는 XML로 반환되니 ResponseEntity<?> 처리로 유연하게 응답을 받던지 try-catch 블록에 XML 파싱 로직을 강구한다. </br>
(참고 : https://kdev.ing/data-go-openapi/)
  
## application.properties 설정
![image](https://github.com/VerifiedIdiot/daily_weather_function_springboot/assets/107241795/fbb8faab-476e-47ec-bf42-3941a9d45d7a)


## 프로젝트 구조
![image](https://github.com/VerifiedIdiot/daily_weather_function_springboot/assets/107241795/4ed4116f-4d8e-436f-9c02-b7bb1db56ec1)

 
## 변경 사항
**플라스크 프로젝트**(https://github.com/VerifiedIdiot/daily_weather_function_flask) 에서 다음과 같은 환경을 적용해 이식하였음
- Java 17
- Spring Boot 3.2.5
- RestClient 
- springdoc-openapi-starter-webmvc-ui:2.0.2
- 캐싱추가

## 개선 사항


# 결과 

# 변경전 


# 변경후 






