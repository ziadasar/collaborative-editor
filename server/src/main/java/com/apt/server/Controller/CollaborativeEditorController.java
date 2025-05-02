package com.apt.controller;

import com.apt.payload.EditMessage;
import com.apt.payload.TextUpdateMessage;
import com.service.DocumentService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class CollaborativeEditorController {

    private final DocumentService documentService;

    public CollaborativeEditorController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @MessageMapping("/edit/{docId}")
    @SendTo("/topic/{docId}") // ✅ Auto-broadcast after method returns
    public TextUpdateMessage handleEdit(
            @DestinationVariable String docId,
            @Payload EditMessage message
    ) {
        System.out.println("📥 Received from frontend: " + message);

        String updatedText;

        if ("insert".equals(message.getType())) {
            System.out.println("✍️ Inserting char: " + message.getCharacter());
            updatedText = documentService.insert(
                    docId,
                    message.getUserId(),
                    message.getCharacter(),
                    message.getPrevId(),
                    message.getNextId()
            );
        } else if ("delete".equals(message.getType())) {
            updatedText = documentService.delete(docId, message.getTargetId());
        } else {
            updatedText = documentService.getText(docId);
        }

        System.out.println("📤 Broadcasting to /topic/" + docId + ": " + updatedText);
        return new TextUpdateMessage(docId, updatedText); // ✅ Will be auto-sent to topic
    }
}
