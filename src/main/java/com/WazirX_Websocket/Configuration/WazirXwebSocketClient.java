package com.WazirX_Websocket.Configuration;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WazirXwebSocketClient extends WebSocketClient{
	
	private ObjectMapper objectMapper = new ObjectMapper();

	private MessageListener listener;
	private final AtomicReference<Double> latestPrice = new AtomicReference<>(0.0);

	private final URI uri;

	public WazirXwebSocketClient(URI uri) {
		super(uri);
		this.uri = uri;
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
	    //System.out.println("Connected to WazirX WebSocket");
		String subscriptionMessage = "{\"event\":\"subscribe\",\"streams\":[\"btcinr@depth5@100ms\"]}";
		this.send(subscriptionMessage);
		//System.out.println("WebSocket connection opened.");
	}

	@Override
	public void onMessage(String message) {
		//System.out.println(message);
		if (listener != null) {
			listener.onMessageReceived(message);
		}
	}

	public void setListener(MessageListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
	    System.out.printf("Disconnected from WazirX WebSocket. Code: %d, Reason: %s, Remote: %b%n", code, reason, remote);

	    // Custom handling based on the close code
	    switch (code) {
	        case 1000: // Normal closure
	            System.out.println("Connection closed normally.");
	            break;
	        case 1001: // Endpoint going away
	            System.out.println("Endpoint is going away.");
	            break;
	        case 1002: // Protocol error
	            System.out.println("Connection closed due to protocol error.");
	            break;
	        case 1003: // Unsupported data
	            System.out.println("Connection closed due to receiving unsupported data.");
	            break;
	        case 1006: // Abnormal closure
	            System.out.println("Connection closed abnormally.");
	            break;
	        case 1007: // Inconsistent data
	            System.out.println("Connection closed due to inconsistent data.");
	            break;
	        case 1008: // Policy violation
	            System.out.println("Connection closed due to policy violation.");
	            break;
	        case 1009: // Message too big
	            System.out.println("Connection closed because the message is too big.");
	            break;
	        case 1010: // Extension negotiation failure
	            System.out.println("Connection closed due to extension negotiation failure.");
	            break;
	        case 1011: // Unexpected condition
	            System.out.println("Connection closed due to unexpected condition.");
	            break;
	        case 1012: // Service restart
	            System.out.println("Connection closed due to service restart.");
	            break;
	        case 1013: // Service overload
	            System.out.println("Connection closed due to service overload.");
	            break;
	        case 1015: // TLS handshake failure
	            System.out.println("Connection closed due to TLS handshake failure.");
	            break;
	        default:
	            System.out.println("Connection closed with an unknown code.");
	            break;
	    }
	}

    @Override
    public void onError(Exception ex) {
	     ex.printStackTrace();
	    }

}
