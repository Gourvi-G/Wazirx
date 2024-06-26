package com.WazirX_Websocket;

import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.WazirX_Websocket.Configuration.MessageListener;
import com.WazirX_Websocket.Configuration.WazirXwebSocketClient;
import com.WazirX_Websocket.Service.WazirXApiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WazirXWebsocketAssignmentApplication implements CommandLineRunner, MessageListener {

    private final WazirXApiService wazirXApiService;
    private final WazirXwebSocketClient wazirXwebSocketClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Double triggerPrice;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final CountDownLatch triggerPriceLatch = new CountDownLatch(1);

    private boolean buyOrderProcessed = false;
    private boolean sellOrderProcessed = false;
    private volatile boolean exitFlag = false;

    @Autowired
    public WazirXWebsocketAssignmentApplication(WazirXApiService wazirXApiService, WazirXwebSocketClient wazirXwebSocketClient) {
        this.wazirXApiService = wazirXApiService;
        this.wazirXwebSocketClient = wazirXwebSocketClient;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WazirXWebsocketAssignmentApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        wazirXwebSocketClient.setListener(this);

        URI serverUri = new URI("wss://stream.wazirx.com/stream");

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Thread for connecting to the WebSocket
        executorService.execute(() -> {
            try {
                wazirXwebSocketClient.connect();
                System.out.println("WebSocket connection opened.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Thread for processing real-time data
        executorService.execute(() -> {
            try {
                triggerPriceLatch.await(); // Wait until the trigger price is set
                while (!exitFlag) {
                    String message = messageQueue.take();
                    processRealTimeData(message);
                }
            } catch (InterruptedException e) {
                if (exitFlag) {
                    System.out.println("Processing thread interrupted due to exit command.");
                } else {
                    e.printStackTrace();
                }
            }
        });

        // Main thread for user input
        Scanner scanner = new Scanner(System.in);

        // Ask for trigger price only once
        System.out.println("Enter Trigger Price");
        triggerPrice = scanner.nextDouble();
        scanner.nextLine(); // Consume newline character
        System.out.println("Trigger price set to: " + triggerPrice);
        triggerPriceLatch.countDown(); // Release the latch to start processing messages

        // Wait for user to type 'exit' to terminate the application
        boolean exit = false;
        while (!exit) {
            String action = scanner.nextLine().trim().toLowerCase();
            if ("exit".equals(action)) {
                exit = true;
                exitFlag = true; // Set the exit flag to stop the processing thread
            } else {
                System.out.println("Invalid action. Please try again.");
            }
        }
        scanner.close();

        // Close the WebSocket connection
        try {
            wazirXwebSocketClient.close();
            System.out.println("WebSocket connection closed.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Shut down the executor service
        executorService.shutdownNow();
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processRealTimeData(String message) {
        try {
            if (triggerPrice == null) {
                return; // Skip processing if trigger price is not set
            }

            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode dataNode = jsonNode.get("data");

            if (dataNode != null) {
                JsonNode askNode = dataNode.get("a");
                JsonNode bidNode = dataNode.get("b");

                // Process sell order when any ask price exceeds or equals trigger price
                if (askNode != null && askNode.isArray()) {
                    for (JsonNode askPriceNode : askNode) {
                        if (askPriceNode.isArray() && askPriceNode.size() > 0) {
                            double askPrice = askPriceNode.get(0).asDouble();
                            if (askPrice >= triggerPrice && !sellOrderProcessed) {
                                wazirXApiService.prepareSellOrder(dataNode);
                                sellOrderProcessed = true;
                                break; // Exit the loop once the order is processed
                            }
                        }
                    }
                }

                // Process buy order when any bid price falls below or equals trigger price
                if (bidNode != null && bidNode.isArray()) {
                    for (JsonNode bidPriceNode : bidNode) {
                        if (bidPriceNode.isArray() && bidPriceNode.size() > 0) {
                            double bidPrice = bidPriceNode.get(0).asDouble();
                            if (bidPrice <= triggerPrice && !buyOrderProcessed) {
                                wazirXApiService.prepareBuyOrder(dataNode);
                                buyOrderProcessed = true;
                                break; // Exit the loop once the order is processed
                            }
                        }
                    }
                }
            } else {
                System.out.println("Data node is null in the received message: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
