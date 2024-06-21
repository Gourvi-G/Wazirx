package com.WazirX_Websocket.Service;

import java.security.InvalidKeyException;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.WazirX_Websocket.Utility.SignatureUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Service
public class WazirXApiService {


    private boolean isBuyOrderPrinted = false;
    private boolean isSellOrderPrinted = false;
    public void prepareBuyOrder(JsonNode dataNode) {
        if (!isBuyOrderPrinted) {
            printBuyOrderDetails(dataNode);
            isBuyOrderPrinted = true;
        }
    }

    public void prepareSellOrder(JsonNode dataNode) {
        if (!isSellOrderPrinted) {
            printSellOrderDetails(dataNode);
            isSellOrderPrinted = true;
        }

    }

    private void printBuyOrderDetails(JsonNode dataNode) {
        System.out.println("Buy order details:");
        System.out.println("Bid: " + dataNode.get("b"));
        System.out.println("Symbol: " + dataNode.get("s"));
    }

    private void printSellOrderDetails(JsonNode dataNode) {
        System.out.println("Sell order details:");
        System.out.println("E: " + dataNode.get("E"));
        System.out.println("Ask: " + dataNode.get("a"));
        System.out.println("Symbol: " + dataNode.get("s"));
    }
}

