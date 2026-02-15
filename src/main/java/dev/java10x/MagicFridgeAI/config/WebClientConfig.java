package dev.java10x.MagicFridgeAI.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${CHATGPT_API_URL}")
    private String chatGptUrl;

    @Bean
    public WebClient webClient(){
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs((ClientCodecConfigurer configurer) ->
                        configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .baseUrl(chatGptUrl)
                .exchangeStrategies(exchangeStrategies)
                .build();
    }
}
