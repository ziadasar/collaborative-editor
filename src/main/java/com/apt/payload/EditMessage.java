package com.apt.payload;

import com.apt.collaborative_editor.Model.PositionIdentifier;

import lombok.Data;

@Data
public class EditMessage {
    private String docId;
    private String userId;
    private String type; // "insert" or "delete"

    private char character; // only for insert

    private PositionIdentifier prevId; // used for insert
    private PositionIdentifier nextId; // used for insert

    private PositionIdentifier targetId; // used for delete
}

