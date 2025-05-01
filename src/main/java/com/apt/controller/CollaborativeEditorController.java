package com.apt.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.apt.payload.EditMessage;
import com.apt.payload.TextUpdateMessage;
import com.service.DocumentService;

@Controller
public class CollaborativeEditorController {

    @Autowired
    private DocumentService documentService;

    // Receive edit message and broadcast updated doc to all users in the doc session
    @MessageMapping("/edit.{docId}")
    @SendTo("/topic/doc.{docId}")
    public TextUpdateMessage handleEdit(
        @DestinationVariable String docId,
        @Payload EditMessage message
    ) {
        System.out.println("Received edit message: " + message);
        System.out.println("Document ID: " + docId);
        String updatedText;

        if ("insert".equals(message.getType())) {
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
            updatedText = documentService.getText(docId); // fallback
        }

        return new TextUpdateMessage(docId, updatedText);
    }
}

