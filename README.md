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
- API KEY 발급([https://apihub.kma.go.kr/](https://www.data.go.kr/iim/api/selectAPIAcountView.do)) 이후에 반드시 디코딩된 key를 사용, 인코딩의경우 URI 클래스 사용으로 전처리 해야함. </br>(참고 : https://velog.io/@cco2416/%EC%A1%B8%EC%97%85%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EA%B3%B5%EA%B3%B5%EB%8D%B0%EC%9D%B4%ED%84%B0-%ED%8F%AC%ED%84%B8-service-key-is-not-registered-error-%ED%95%B4%EA%B2%B0-%EB%B0%A9%EB%B2%95)
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






