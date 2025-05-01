package com.apt.collaborative_editor.Model.CRDT.Operations;

import java.util.UUID;

import com.apt.collaborative_editor.Model.CRDT.CRDTDocument;

public class InsertOperation implements CRDTOperation {
    private final UUID userId;
    private final long timestamp;
    private final char value;
    private final UUID afterNodeId;

    public InsertOperation(UUID userId, long timestamp, char value, UUID afterNodeId) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.value = value;
        this.afterNodeId = afterNodeId;
    }

    @Override
    public void apply(CRDTDocument document) {
        document.insert(value, userId, afterNodeId);
    }

    @Override
    public UUID getUserId() {
        return userId;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public char getValue() {
        return value;
    }

    public UUID getAfterNodeId() {
        return afterNodeId;
    }
}