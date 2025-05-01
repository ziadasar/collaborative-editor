package com.apt.collaborative_editor.Model.Network;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.apt.collaborative_editor.Model.CRDT.CRDTDocument;

public class DocumentManager {
    private final Map<UUID, CRDTDocument> documents;
    private final Map<String, UUID> editorCodes;
    private final Map<String, UUID> viewerCodes;

    public DocumentManager() {
        this.documents = new ConcurrentHashMap<>();
        this.editorCodes = new ConcurrentHashMap<>();
        this.viewerCodes = new ConcurrentHashMap<>();
    }

    public UUID createNewDocument() {
        UUID docId = UUID.randomUUID();
        documents.put(docId, new CRDTDocument(docId));

        // Generate access codes
        String editorCode = generateUniqueCode();
        String viewerCode = generateUniqueCode();

        editorCodes.put(editorCode, docId);
        viewerCodes.put(viewerCode, docId);

        return docId;
    }

    public Optional<CRDTDocument> getDocumentByEditorCode(String code) {
        return Optional.ofNullable(editorCodes.get(code))
                .map(documents::get);
    }

    public Optional<CRDTDocument> getDocumentByViewerCode(String code) {
        return Optional.ofNullable(viewerCodes.get(code))
                .map(documents::get);
    }

    public Optional<String> getEditorCode(UUID docId) {
        return editorCodes.entrySet().stream()
                .filter(e -> e.getValue().equals(docId))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public Optional<String> getViewerCode(UUID docId) {
        return viewerCodes.entrySet().stream()
                .filter(e -> e.getValue().equals(docId))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}