package com.apt.collaborative_editor.Model.CRDT;

import java.util.*;
import java.util.stream.Collectors;

public class CRDTDocument {
    private final UUID documentId;
    private final Map<UUID, CRDTNode> nodes;
    private final Map<UUID, List<UUID>> childMap;
    private final UUID rootId;
    private long logicalClock;

    public CRDTDocument(UUID documentId) {
        this.documentId = documentId;
        this.nodes = new HashMap<>();
        this.childMap = new HashMap<>();
        this.rootId = UUID.randomUUID();
        this.logicalClock = 0;

        // Create root node
        CRDTNode root = new CRDTNode(UUID.randomUUID(), 0, null, null);
        nodes.put(rootId, root);
        childMap.put(rootId, new ArrayList<>());
    }

    // Insert a character after a specific position
    public synchronized CRDTNode insert(char value, UUID userId, UUID afterNodeId) {
        logicalClock++;
        CRDTNode parent = nodes.get(afterNodeId != null ? afterNodeId : rootId);

        CRDTNode newNode = new CRDTNode(userId, logicalClock, value, parent.getId());
        nodes.put(newNode.getId(), newNode);

        // Update child map
        if (!childMap.containsKey(parent.getId())) {
            childMap.put(parent.getId(), new ArrayList<>());
        }
        childMap.get(parent.getId()).add(newNode.getId());

        return newNode;
    }

    // Delete a character
    public synchronized void delete(UUID nodeId) {
        if (nodes.containsKey(nodeId)) {
            nodes.get(nodeId).delete();
        }
    }

    // Generate the current document text
    public synchronized String getText() {
        StringBuilder sb = new StringBuilder();
        traverse(rootId, sb);
        return sb.toString();
    }

    // Depth-first traversal to build the document
    private void traverse(UUID nodeId, StringBuilder sb) {
        List<UUID> children = childMap.getOrDefault(nodeId, Collections.emptyList());

        // Sort children by timestamp (descending) and then by userId (ascending)
        children.sort((a, b) -> {
            CRDTNode nodeA = nodes.get(a);
            CRDTNode nodeB = nodes.get(b);
            int timeCompare = Long.compare(nodeB.getTimestamp(), nodeA.getTimestamp());
            if (timeCompare != 0)
                return timeCompare;
            return nodeA.getUserId().compareTo(nodeB.getUserId());
        });

        for (UUID childId : children) {
            CRDTNode child = nodes.get(childId);
            if (!child.isDeleted()) {
                if (child.getValue() != null) {
                    sb.append(child.getValue());
                }
                traverse(childId, sb);
            }
        }
    }

    // Find the node at a specific position in the document
    public synchronized UUID findNodeAtPosition(int position) {
        List<UUID> path = new ArrayList<>();
        findPosition(rootId, position, path);
        return path.isEmpty() ? null : path.get(path.size() - 1);
    }

    private int findPosition(UUID nodeId, int remaining, List<UUID> path) {
        List<UUID> children = childMap.getOrDefault(nodeId, Collections.emptyList());

        // Sort children as in traverse
        children.sort((a, b) -> {
            CRDTNode nodeA = nodes.get(a);
            CRDTNode nodeB = nodes.get(b);
            int timeCompare = Long.compare(nodeB.getTimestamp(), nodeA.getTimestamp());
            if (timeCompare != 0)
                return timeCompare;
            return nodeA.getUserId().compareTo(nodeB.getUserId());
        });

        for (UUID childId : children) {
            CRDTNode child = nodes.get(childId);
            if (!child.isDeleted() && child.getValue() != null) {
                if (remaining == 0) {
                    path.add(childId);
                    return 0;
                }
                remaining--;
            }

            remaining = findPosition(childId, remaining, path);
            if (!path.isEmpty())
                return remaining;
        }

        return remaining;
    }

    // Get all nodes (for serialization)
    public synchronized Collection<CRDTNode> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }

    // Add a node (for deserialization)
    public synchronized void addNode(CRDTNode node) {
        nodes.put(node.getId(), node);
        if (!childMap.containsKey(node.getParentId())) {
            childMap.put(node.getParentId(), new ArrayList<>());
        }
        childMap.get(node.getParentId()).add(node.getId());
        logicalClock = Math.max(logicalClock, node.getTimestamp());
    }
}