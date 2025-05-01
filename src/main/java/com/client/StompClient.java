package com.client;

import com.apt.collaborative_editor.Model.PositionIdentifier;
import com.apt.payload.EditMessage;
import com.apt.payload.TextUpdateMessage;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

public class StompClient {

    private StompSession stompSession;
    private final Gson gson = new Gson();
    private final TextArea editor;
    private final String docId;
    private final String userId;

    public StompClient(TextArea editor, String docId, String userId) {
        this.editor = editor;
        this.docId = docId;
        this.userId = userId;
    }

    public void connect() {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());

        ListenableFuture<StompSession> future = stompClient.connect(
                "ws://localhost:8080/ws",
                new WebSocketHttpHeaders(),
                new StompSessionHandlerAdapter() {}
        );

        try {
            stompSession = future.get();

            stompSession.subscribe("/topic/doc." + docId, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return String.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    String message = (String) payload;
                    TextUpdateMessage update = gson.fromJson(message, TextUpdateMessage.class);
                    Platform.runLater(() -> editor.setText(update.getUpdatedText()));
                }
            });

            System.out.println("✅ Subscribed to /topic/doc." + docId);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void sendInsert(char c) {
        EditMessage msg = new EditMessage();
        msg.setType("insert");
        msg.setDocId(docId);
        msg.setUserId(userId);
        msg.setCharacter(c);
        msg.setPrevId(new PositionIdentifier());
        msg.setNextId(new PositionIdentifier());

        String json = gson.toJson(msg);
        stompSession.send("/app/edit." + docId, json);
    }
}
