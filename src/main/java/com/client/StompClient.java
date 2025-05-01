package com.client;

import com.apt.collaborative_editor.Model.PositionIdentifier;
import com.apt.payload.EditMessage;
import com.apt.payload.TextUpdateMessage;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class StompClient {

    private StompSession stompSession;
    private final TextArea editor;
    private final String docId;
    private final String userId;

    public StompClient(TextArea editor, String docId, String userId) {
        this.editor = editor;
        this.docId = docId;
        this.userId = userId;
    }

    public void connect() {
        System.out.println("🌐 Connecting to WebSocket server (SockJS)...");

        // Create SockJS-enabled client
        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter()); // ✅ Handles JSON

        CompletableFuture<StompSession> future = new CompletableFuture<>();

        stompClient.connect("http://localhost:8080/ws", new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println("✅ Connected to WebSocket server via SockJS");
                future.complete(session);
            }
            
            
            

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                future.completeExceptionally(exception);
                exception.printStackTrace();
            }
        });

        try {
            stompSession = future.get();

            // ✅ Subscribe to the topic
            stompSession.subscribe("/topic/" + docId, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return TextUpdateMessage.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    TextUpdateMessage update = (TextUpdateMessage) payload;
                    System.out.println("⬅️ Received update from server: " + update);
                    Platform.runLater(() -> editor.setText(update.getUpdatedText()));
                }
            });

            System.out.println("✅ Subscribed to /topic/" + docId);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void sendInsert(char c) {
        if (stompSession == null) {
            System.err.println("❌ Cannot send message: Not connected");
            return;
        }

        System.out.println("➡️ Sending insert to /app/edit/" + docId + ": " + c);

        EditMessage msg = new EditMessage();
        msg.setType("insert");
        msg.setDocId(docId);
        msg.setUserId(userId);
        msg.setCharacter(c);
        msg.setPrevId(new PositionIdentifier());
        msg.setNextId(new PositionIdentifier());
        System.out.println("Sending message: next" + "/app/edit/" + docId); // ✅ Log the message being sent
        stompSession.send("/app/edit/" + docId, msg); // ✅ Send actual object, not JSON
    }
}
