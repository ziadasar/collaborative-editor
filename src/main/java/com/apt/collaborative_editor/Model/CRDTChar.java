package com.apt.collaborative_editor.Model;

import lombok.*;

@Data
@AllArgsConstructor
public class CRDTChar {
    private char value;
    private PositionIdentifier id;
    private boolean tombstone;
}
