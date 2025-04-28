package com.apt.collaborative_editor.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CRDTNode {
    private final CharId id;
    private char value;
    private boolean isTombstone; // Marks deleted characters
    private CRDTNode next;

    public CRDTNode(CharId id, char value) {
        this.id = id;
        this.value = value;
        this.isTombstone = false;
    }

    // Getters/setters (omitted)
}