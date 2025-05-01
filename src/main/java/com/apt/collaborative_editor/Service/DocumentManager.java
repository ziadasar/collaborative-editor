package com.apt.collaborative_editor.Service;

import org.springframework.stereotype.Service;

import com.apt.collaborative_editor.Model.CRDT.CRDTDocument;
import com.apt.collaborative_editor.Model.CRDT.Operations.CRDTOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.*;

@Service
public class DocumentManager {
    private final Map<UUID, CRDTDocument> documents = new HashMap<>();
    private final Map<String, UUID> editorCodes = new HashMap<>();
    private final Map<String, UUID> viewerCodes = new HashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public UUID createNewDocument() {
        UUID docId = UUID.randomUUID();
        documents.put(docId, new CRDTDocument(docId));
        generateAccessCodes(docId);
        return docId;
    }

    private void generateAccessCodes(UUID docId) {
        editorCodes.put(generateCode(), docId);
        viewerCodes.put(generateCode(), docId);
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void broadcastUpdate(UUID docId, CRDTOperation operation) {
        messagingTemplate.convertAndSend("/topic/updates/" + docId, operation);
    }
}