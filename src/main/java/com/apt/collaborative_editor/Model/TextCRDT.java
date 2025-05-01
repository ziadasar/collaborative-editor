package com.apt.collaborative_editor.Model;

import java.util.ArrayList;
import java.util.List;

import com.apt.collaborative_editor.PositionIdentifierGenerator;
import java.util.Comparator;
import java.util.List;

public class TextCRDT {

    private final String userId; // The user performing local operations
    private final List<CRDTChar> sequence; // The list of characters (ordered by ID)

    public TextCRDT(String userId) {
        this.userId = userId;
        this.sequence = new ArrayList<>();
    }

    // Insert a new character between two existing position identifiers
    public void insert(char c, PositionIdentifier prevId, PositionIdentifier nextId) {
        // Generate a unique ID between the two
        PositionIdentifier newId = PositionIdentifierGenerator.generateBetween(prevId, nextId, userId);
        // Create the new CRDT character
        CRDTChar newChar = new CRDTChar(c, newId, false);
        // Add to list
        sequence.add(newChar);
        // Sort all characters to maintain CRDT order
        sequence.sort(Comparator.comparing(CRDTChar::getId));
    }

    // Tombstone (logically delete) a character with a specific ID
    public void delete(PositionIdentifier id) {
        for (CRDTChar ch : sequence) {
            if (ch.getId().equals(id)) {
                ch.setTombstone(true);
                break;
            }
        }
    }

    // Convert current CRDT state to visible document text (skipping tombstoned chars)
    public String toPlainText() {
        StringBuilder sb = new StringBuilder();
        for (CRDTChar ch : sequence) {
            if (!ch.isTombstone()) {
                sb.append(ch.getValue());
            }
        }
        return sb.toString();
    }

    // Optional: Return full internal state (useful for debugging)
    public List<CRDTChar> getAllCharacters() {
        return sequence;
    }
}
