package com.apt.collaborative_editor.Model.CRDT;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.apt.collaborative_editor.Model.CRDT.Operations.CRDTOperation;
import com.apt.collaborative_editor.Model.CRDT.Operations.DeleteOperation;
import com.apt.collaborative_editor.Model.CRDT.Operations.InsertOperation;

public class CRDTDocument {
    private final UUID documentId;
    private final Map<UUID, CRDTNode> nodes;
    private final Map<UUID, List<UUID>> childMap;
    private final UUID rootId;
    private final AtomicLong logicalClock;
    private transient final Object lock = new Object();

    public CRDTDocument(UUID documentId) {
        this.documentId = documentId;
        this.nodes = new ConcurrentHashMap<>();
        this.childMap = new ConcurrentHashMap<>();
        this.rootId = UUID.randomUUID();
        this.logicalClock = new AtomicLong(0);
        initializeRootNode();
    }

    private void initializeRootNode() {
        CRDTNode root = new CRDTNode(UUID.randomUUID(), 0, null, null);
        nodes.put(rootId, root);
        childMap.put(rootId, Collections.synchronizedList(new ArrayList<>()));
    }

    public CRDTNode insert(int value, char userId, UUID afterNodeId) {
        synchronized (lock) {
            long timestamp = logicalClock.incrementAndGet();
            UUID parentId = (afterNodeId != null) ? afterNodeId : rootId;
            CRDTNode parent = nodes.get(parentId);

            if (parent == null) {
                throw new IllegalStateException("Parent node not found");
            }

            CRDTNode newNode = new CRDTNode(userId, timestamp, value, parent.getId());
            nodes.put(newNode.getId(), newNode);
            childMap.computeIfAbsent(parent.getId(), k -> Collections.synchronizedList(new ArrayList<>()))
                    .add(newNode.getId());

            return newNode;
        }
    }

    public void delete(UUID nodeId) {
        synchronized (lock) {
            CRDTNode node = nodes.get(nodeId);
            if (node != null) {
                node.delete();
            }
        }
    }

    public String getText() {
        synchronized (lock) {
            StringBuilder sb = new StringBuilder();
            traverse(rootId, sb);
            return sb.toString();
        }
    }

    private void traverse(UUID nodeId, StringBuilder sb) {
        List<UUID> children = childMap.getOrDefault(nodeId, Collections.emptyList());

        children.stream()
                .sorted(this::compareNodes)
                .forEach(childId -> {
                    CRDTNode child = nodes.get(childId);
                    if (!child.isDeleted() && child.getValue() != null) {
                        sb.append(child.getValue());
                        traverse(childId, sb);
                    }
                });
    }

    private int compareNodes(UUID a, UUID b) {
        CRDTNode nodeA = nodes.get(a);
        CRDTNode nodeB = nodes.get(b);
        int timeCompare = Long.compare(nodeB.getTimestamp(), nodeA.getTimestamp());
        return (timeCompare != 0) ? timeCompare : nodeA.getUserId().compareTo(nodeB.getUserId());
    }

    public UUID findNodeAtPosition(int position) {
        synchronized (lock) {
            if (position < 0)
                return null;

            AtomicInteger counter = new AtomicInteger(0);
            UUID[] result = new UUID[1];
            findPosition(rootId, position, counter, result);
            return result[0];
        }
    }

    private void findPosition(UUID nodeId, int targetPos,
            AtomicInteger currentPos, UUID[] result) {
        childMap.getOrDefault(nodeId, Collections.emptyList())
                .stream()
                .sorted(this::compareNodes)
                .forEach(childId -> {
                    if (result[0] != null)
                        return;

                    CRDTNode child = nodes.get(childId);
                    if (!child.isDeleted() && child.getValue() != null) {
                        if (currentPos.getAndIncrement() == targetPos) {
                            result[0] = childId;
                            return;
                        }
                        findPosition(childId, targetPos, currentPos, result);
                    }
                });
    }

    // In CRDTDocument.java
    public synchronized void applyOperation(CRDTOperation operation) {
        if (operation instanceof InsertOperation) {
            InsertOperation insertOp = (InsertOperation) operation;
            this.insert(insertOp.getValue(), insertOp.getUserId(), insertOp.getAfterNodeId());
        } else if (operation instanceof DeleteOperation) {
            DeleteOperation deleteOp = (DeleteOperation) operation;
            this.delete(deleteOp.getNodeId());
        }
    }

    public Collection<CRDTNode> getAllNodes() {
        synchronized (lock) {
            return new ArrayList<>(nodes.values());
        }
    }

    public void addNode(CRDTNode node) {
        synchronized (lock) {
            nodes.put(node.getId(), node);
            childMap.computeIfAbsent(node.getParentId(), k -> Collections.synchronizedList(new ArrayList<>()))
                    .add(node.getId());
            logicalClock.set(Math.max(logicalClock.get(), node.getTimestamp()));
        }
    }

    // Additional helpful methods
    public int getDocumentLength() {
        synchronized (lock) {
            return getText().length();
        }
    }

    public boolean containsNode(UUID nodeId) {
        synchronized (lock) {
            return nodes.containsKey(nodeId) && !nodes.get(nodeId).isDeleted();
        }
    }
}