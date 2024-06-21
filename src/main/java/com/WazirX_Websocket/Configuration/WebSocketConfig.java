package com.WazirX_Websocket.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class WebSocketConfig {

    @Bean
    public URI wazirXUri() throws URISyntaxException {
        return new URI("wss://stream.wazirx.com/stream");
    }
}
