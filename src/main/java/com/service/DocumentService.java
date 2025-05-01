package com.service;



import org.springframework.stereotype.Service;

import com.apt.collaborative_editor.Model.PositionIdentifier;
import com.apt.collaborative_editor.Model.TextCRDT;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class DocumentService {

    // Each document ID maps to one CRDT instance
    private final ConcurrentHashMap<String, TextCRDT> documents = new ConcurrentHashMap<>();

    // Ensure document exists before modifying it
    private TextCRDT getOrCreate(String docId, String userId) {
        System.out.println("getOrCreate called with docId: " + docId + ", userId: " + userId);
        documents.putIfAbsent(docId, new TextCRDT(userId));
        return documents.get(docId);
    }

    // Handle insert operation
    public String insert(String docId, String userId, char c, PositionIdentifier prev, PositionIdentifier next) {
        System.out.println("Insert called with docId: " + docId + ", userId: " + userId + ", char: " + c);
        TextCRDT crdt = getOrCreate(docId, userId);
        crdt.insert(c, prev, next);
        return crdt.toPlainText(); // Return updated state for broadcasting
    }

    // Handle delete operation
    public String delete(String docId, PositionIdentifier id) {
        System.out.println("Delete called with docId: " + docId + ", id: " + id);
        TextCRDT crdt = documents.get(docId);
        if (crdt != null) {
            crdt.delete(id);
            return crdt.toPlainText();
        }
        return null;
    }

    // Get the latest version of the document
    public String getText(String docId) {
        TextCRDT crdt = documents.get(docId);
        if (crdt != null) {
            return crdt.toPlainText();
        }
        return "";
    }
}
