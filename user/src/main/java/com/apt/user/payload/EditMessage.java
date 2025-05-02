package com.apt.payload;

import com.apt.collaborative_editor.Model.PositionIdentifier;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // ✅ Add this
public class EditMessage {
    private String docId;
    private String userId;
    private String type;
    private char character;
    private PositionIdentifier prevId;
    private PositionIdentifier nextId;
    private PositionIdentifier targetId;
}
