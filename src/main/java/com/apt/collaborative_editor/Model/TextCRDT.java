package com.apt.collaborative_editor.Model;

import java.util.ArrayList;
import java.util.List;
// import com.apt.collaborative_editor.Model.CharId;
// import com.apt.collaborative_editor.Model.CRDTNode;

public class TextCRDT {
    private CRDTNode head; // Dummy head node
    private String userId; // Current user's ID

    public TextCRDT(String userId) {
        this.userId = userId;
        this.head = new CRDTNode(new CharId(Integer.MIN_VALUE, "", 0), '\0');
    }

    // Insert a character at a position
    public void insert(int pos, char c, String userId) {
        CharId newId = generateId(pos, userId);
        CRDTNode newNode = new CRDTNode(newId, c);
        CRDTNode current = findInsertionPoint(pos, userId);
        // newNode.next = current.next;
        // current.next = newNode;
        newNode.setNext(current.getNext());
        current.setNext(newNode);
    }

    // Delete a character at a position
    public void delete(int pos) {
        CRDTNode node = findNodeAtPosition(pos);
        if (node != null) {
            // node.isTombstone = true;
            node.setTombstone(true);
        }
    }

    // Generate a unique ID for a new character
    private CharId generateId(int pos, String userId) {
        return new CharId(pos, userId, System.nanoTime());
    }

    // Find where to insert a new character
    private CRDTNode findInsertionPoint(int pos, String userId) {
        CRDTNode current = head;
        // while (current.next != null && comparePositions(current.next.id, pos, userId)
        // < 0) {
        while (current.getNext() != null && comparePositions(current.getNext().getId(), pos, userId) < 0) {

            current = current.getNext();
        }
        return current;
    }

    // Compare positions (fractional indexing logic)
    private int comparePositions(CharId id, int pos, String userId) {
        if (id.getPosition() != pos) {
            return Integer.compare(id.getPosition(), pos);
        } else {
            return id.getUserId().compareTo(userId);
        }
    }

    // Convert CRDT state to a string (skips tombstones)
    public String toString() {
        StringBuilder sb = new StringBuilder();
        CRDTNode current = head.getNext();
        while (current != null) {
            if (!current.isTombstone()) {
                sb.append(current.getValue());
            }
            current = current.getNext();
        }
        return sb.toString();
    }

    /**
     * Finds the node at a visible (non-tombstone) position in the document.
     * Returns null if position is invalid.
     */
    private CRDTNode findNodeAtPosition(int targetPos) {
        CRDTNode current = head.getNext();
        int visiblePos = 0; // Tracks non-tombstone positions

        while (current != null) {
            if (!current.isTombstone()) {
                if (visiblePos == targetPos) {
                    return current; // Found the node at the target position
                }
                visiblePos++;
            }
            current = current.getNext();
        }
        return null; // Position out of bounds
    }
}