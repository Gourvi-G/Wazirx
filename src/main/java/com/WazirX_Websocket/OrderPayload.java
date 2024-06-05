package com.WazirX_Websocket;

	public class OrderPayload {

	    private String type;
	    private String symbol;
	    private double price;
	    private double quantity;

	    public OrderPayload(String type, String symbol, double price, double quantity) {
	        this.type = type;
	        this.symbol = symbol;
	        this.price = price;
	        this.quantity = quantity;
	    }

	    // Getters and Setters
	}

	public class WazirXWebSocketClient extends WebSocketClient {

	    private double triggerPrice;

	    public WazirXWebSocketClient(URI serverUri, double triggerPrice) {
	        super(serverUri);
	        this.triggerPrice = triggerPrice;
	    }

	    @Override
	    public void onMessage(String message) {
	        System.out.println("Received message: " + message);

	        // Parse the message and extract the market price
	        double marketPrice = extractMarketPrice(message);

	        if (marketPrice <= triggerPrice) {
	            OrderPayload buyOrder = new OrderPayload("buy", "btcinr", marketPrice, 1);
	            System.out.println("Prepared buy order payload: " + new Gson().toJson(buyOrder));
	        } else if (marketPrice >= triggerPrice) {
	            OrderPayload sellOrder = new OrderPayload("sell", "btcinr", marketPrice, 1);
	            System.out.println("Prepared sell order payload: " + new Gson().toJson(sellOrder));
	        }
	    }

	    private double extractMarketPrice(String message) {
	        // Implement the logic to parse the message and extract the market price
	        // This is a placeholder implementation
	        return 0.0;
	    }

	    // Other overridden methods...
	}



