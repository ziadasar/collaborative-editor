package com.apt.collaborative_editor.Controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import com.example.collaborativeeditor.model.crdt.CRDTOperation;
import com.example.collaborativeeditor.service.DocumentManager;
import com.example.collaborativeeditor.util.CRDTSerializer;

@Controller
public class CollaborationController {

    @Autowired
    private DocumentManager documentManager;

    @Autowired
    private CRDTSerializer serializer;

    @MessageMapping("/collab/{docId}/{userId}/{code}")
    public void handleOperation(
            @DestinationVariable UUID docId,
            @DestinationVariable UUID userId,
            String message) {
        try {
            CRDTOperation op = serializer.deserialize(message);
            documentManager.applyOperation(docId, op);
        } catch (JsonParseException e) {
            // Spring-specific JSON handling
            System.err.println("Invalid operation format: " + e.getMessage());
        }
    }
}