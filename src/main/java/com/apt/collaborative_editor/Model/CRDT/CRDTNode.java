package com.apt.collaborative_editor.Model.CRDT;

import java.util.UUID;

public class CRDTNode {
    private final UUID id;
    private final UUID userId;
    private final long timestamp;
    private final Character value;
    private final UUID parentId;
    private boolean deleted;

    public CRDTNode(UUID userId, long timestamp, Character value, UUID parentId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.timestamp = timestamp;
        this.value = value;
        this.parentId = parentId;
        this.deleted = false;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Character getValue() {
        return value;
    }

    public UUID getParentId() {
        return parentId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    // Mark as deleted
    public void delete() {
        this.deleted = true;
    }

    // Restore the node
    public void restore() {
        this.deleted = false;
    }

    @Override
    public String toString() {
        return "CRDTNode{" +
                "id=" + id +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                ", value=" + value +
                ", parentId=" + parentId +
                ", deleted=" + deleted +
                '}';
    }
}