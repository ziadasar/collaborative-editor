package com.apt.collaborative_editor.Model.CRDT.Operations;

import java.util.UUID;

import com.apt.collaborative_editor.Model.CRDT.CRDTDocument;

public class DeleteOperation implements CRDTOperation {
    private final UUID userId;
    private final long timestamp;
    private final UUID nodeId;

    public DeleteOperation(UUID userId, long timestamp, UUID nodeId) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.nodeId = nodeId;
    }

    @Override
    public void apply(CRDTDocument document) {
        document.delete(nodeId);
    }

    @Override
    public UUID getUserId() {
        return userId;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public UUID getNodeId() {
        return nodeId;
    }
}