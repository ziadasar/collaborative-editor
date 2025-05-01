package com.apt.collaborative_editor;

import com.apt.collaborative_editor.Model.CRDT.CRDTDocument;
import java.util.UUID;

public class TestCRDT {
    public static void main(String[] args) {
        CRDTDocument doc = new CRDTDocument(UUID.randomUUID()); // Use a valid UUID
        doc.insert(0, 'a', UUID.randomUUID());
        doc.insert(1, 'b', UUID.randomUUID());
        doc.insert(2, 'c', UUID.randomUUID());

        doc.delete(doc.findNodeAtPosition(1)); // Deletes 'b' (now "a c")
        System.out.println(doc.getText()); // Output: "ac"
    }
}