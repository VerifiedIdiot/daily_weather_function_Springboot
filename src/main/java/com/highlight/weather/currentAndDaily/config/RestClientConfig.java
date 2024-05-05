package com.highlight.weather.currentAndDaily.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;


@Configuration
public class RestClientConfig {


    @Bean
    public RestClient restClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(this::acceptedCodecs)
                .build();

        return RestClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private void acceptedCodecs(ClientCodecConfigurer clientCodecConfigurer) {
        clientCodecConfigurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
    }

}
