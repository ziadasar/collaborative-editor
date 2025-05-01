package com.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CollaborativeEditorClient extends Application {

    private TextArea editor;
    private StompClient stompClient;
    private static final String USER_ID = "user123";
    private static final String DOC_ID = "demo-doc";

    @Override
    public void start(Stage stage) {
        editor = new TextArea();
        editor.setPromptText("Type here...");

        stompClient = new StompClient(editor, DOC_ID, USER_ID);
        stompClient.connect();

        editor.setOnKeyTyped(e -> {
            String input = e.getCharacter();
            if (input.length() == 1) {
                stompClient.sendInsert(input.charAt(0));
            }
        });

        VBox root = new VBox(editor);
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Collaborative Editor (STOMP)");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
