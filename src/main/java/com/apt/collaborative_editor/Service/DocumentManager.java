package com.apt.collaborative_editor.Service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.apt.collaborative_editor.Model.CRDT.CRDTDocument;
import com.apt.collaborative_editor.Model.CRDT.Operations.CRDTOperation;

@Service
public class DocumentManager {
    // Thread-safe collections for concurrent access
    private final Map<UUID, CRDTDocument> documents = new ConcurrentHashMap<>();
    private final Map<String, UUID> editorCodes = new ConcurrentHashMap<>();
    private final Map<String, UUID> viewerCodes = new ConcurrentHashMap<>();
    private final Map<UUID, String> reverseEditorCodes = new ConcurrentHashMap<>();
    private final Map<UUID, String> reverseViewerCodes = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public UUID createNewDocument() {
        UUID docId = UUID.randomUUID();
        documents.put(docId, new CRDTDocument(docId));
        generateAccessCodes(docId);
        return docId;
    }

    private void generateAccessCodes(UUID docId) {
        String editorCode = generateCode();
        String viewerCode = generateCode();

        editorCodes.put(editorCode, docId);
        viewerCodes.put(viewerCode, docId);
        reverseEditorCodes.put(docId, editorCode);
        reverseViewerCodes.put(docId, viewerCode);
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // In DocumentManager.java
    public void applyOperation(UUID docId, CRDTOperation operation) {
        CRDTDocument doc = documents.get(docId);
        if (doc != null) {
            doc.applyOperation(operation); // Now this will work
            broadcastUpdate(docId, operation);
        } else {
            throw new IllegalArgumentException("Document not found: " + docId);
        }
    }

    public void broadcastUpdate(UUID docId, CRDTOperation operation) {
        messagingTemplate.convertAndSend("/topic/updates/" + docId, operation);
    }

    // Additional useful methods
    public Optional<CRDTDocument> getDocument(UUID docId) {
        return Optional.ofNullable(documents.get(docId));
    }

    public Optional<UUID> getDocumentIdByEditorCode(String code) {
        return Optional.ofNullable(editorCodes.get(code));
    }

    public Optional<UUID> getDocumentIdByViewerCode(String code) {
        return Optional.ofNullable(viewerCodes.get(code));
    }

    public Optional<String> getEditorCode(UUID docId) {
        return Optional.ofNullable(reverseEditorCodes.get(docId));
    }

    public Optional<String> getViewerCode(UUID docId) {
        return Optional.ofNullable(reverseViewerCodes.get(docId));
    }
}