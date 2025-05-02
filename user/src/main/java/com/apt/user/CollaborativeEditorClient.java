package com.apt.user;
// import com.apt.user.stomp.StompClient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.apt.user.UserWebsocket;

public class CollaborativeEditorClient extends Application {
    static final String BASE_URL = "http://localhost:8080/";
    static final String WS_URL = "http://localhost:8080/ws";

    private TextArea editor;

    private static String USER_ID = "user123";
    private static String DOC_ID = "demo-doc";
    PollWebsocket websocket = new PollWebsocket(WS_URL);

    @Override
    public void start(Stage stage) {
        editor = new TextArea();
        editor.setPromptText("Type here...");

        stompClient = new StompClient(editor, DOC_ID, USER_ID);
        stompClient.connect();

        editor.setOnKeyTyped(e -> {
            String input = e.getCharacter();
            if (input.length() == 1) {
                System.out.println("✍️ Sending char: " + input.charAt(0));
                stompClient.sendInsert(input.charAt(0));
            }
        });

        VBox root = new VBox(editor);
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Collaborative Editor ");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
