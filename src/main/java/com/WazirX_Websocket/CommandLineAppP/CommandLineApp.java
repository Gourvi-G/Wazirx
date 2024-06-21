package com.WazirX_Websocket.CommandLineAppP;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.WazirX_Websocket.Configuration.WazirXwebSocketClient;


import java.net.URI;
import java.net.URISyntaxException;

@Component
public class CommandLineApp implements CommandLineRunner {
 
    
    
    @Autowired
    private WazirXwebSocketClient Wazirx;

    @Override
    public void run(String... args) throws Exception {
        try {
        	WazirXwebSocketClient client = new WazirXwebSocketClient(new URI("wss://stream.wazirx.com/stream"));
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
