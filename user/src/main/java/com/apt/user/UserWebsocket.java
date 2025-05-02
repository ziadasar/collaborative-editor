package com.apt.user;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

public class UserWebsocket {
    private StompSession stompSession;
    private final String websocketUrl;

    public UserWebsocket(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }

    public void connectToWebSocket() {
        try {
            List<Transport> transports = Collections.singletonList(
                    new WebSocketTransport(new StandardWebSocketClient()));

            SockJsClient sockJsClient = new SockJsClient(transports);
            WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());

            this.stompSession = stompClient.connect(websocketUrl, new StompSessionHandlerAdapter() {
            })
                    .get();

            System.out.println("Connected to WebSocket server at " + websocketUrl);
        } catch (Exception e) {
            System.err.println("Error connecting to WebSocket server:");
            e.printStackTrace();
        }
    }

    public void insertCharacter(String option, String pollId) {
        // TODO: write the code for sending the vote
        if (stompSession == null || !stompSession.isConnected()) {
            System.err.println("Not connected to WebSocket server");
            return;
        }

        stompSession.send("/app/polls/" + pollId + "/vote", option);
        System.out.println("Vote sent for option: " + option);
    }

    public void subscribeToDoc(String docId) {
        // TODO: Subscribe to the poll topic. Print PollResult updates in the format
        // shown in document
        if (stompSession == null || !stompSession.isConnected()) {
            System.err.println("Not connected to WebSocket server");
            return;
        }

        stompSession.subscribe("/topic/" + docId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received message: " + payload);
                // PollResult result = (PollResult) payload;
                // System.out.println("\n=== Poll Update ===");
                // System.out.println("Poll ID: " + pollId);

                // for (int i = 0; i < result.options().size(); i++) {
                // String option = result.options().get(i);
                // double percentage = result.percentages().get(i).val();
                // System.out.printf("%s: %.1f%%\n", option, percentage);
                // }
                // System.out.println("==================\n");
            }
        });

    }

    public void close() {
        this.stompSession.disconnect();
        System.out.println("Disconnected from WebSocket server");
    }
}
