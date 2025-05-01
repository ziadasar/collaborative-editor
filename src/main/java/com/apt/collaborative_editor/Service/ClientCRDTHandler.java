package com.apt.collaborative_editor.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import com.apt.collaborative_editor.Model.CRDT.CRDTDocument;
import com.apt.collaborative_editor.Model.CRDT.CRDTNode;
import com.apt.collaborative_editor.Model.CRDT.Operations.*;
import com.apt.collaborative_editor.Model.Util.CRDTSerializer;

@Service
public class ClientCRDTHandler {
    private final UUID userId;
    private final CRDTDocument localDocument;
    private final AtomicLong localClock;
    private final Stack<CRDTOperation> undoStack;
    private final Stack<CRDTOperation> redoStack;

    @Autowired
    private StompSession stompSession;

    @Autowired
    private CRDTSerializer serializer;

    public ClientCRDTHandler() {
        this.userId = UUID.randomUUID(); // Auto-generated for Spring bean
        this.localDocument = new CRDTDocument(UUID.randomUUID());
        this.localClock = new AtomicLong(0);
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    public InsertOperation handleLocalInsert(char value, int position) {
        UUID afterNodeId = position == 0 ? null : localDocument.findNodeAtPosition(position - 1);
        long timestamp = localClock.incrementAndGet();

        InsertOperation op = new InsertOperation(userId, timestamp, value, afterNodeId);
        op.apply(localDocument);
        undoStack.push(op);
        redoStack.clear();

        sendOperation(op); // Send via WebSocket
        return op;
    }

    public DeleteOperation handleLocalDelete(int position) {
        UUID nodeId = localDocument.findNodeAtPosition(position);
        if (nodeId == null)
            return null;

        long timestamp = localClock.incrementAndGet();
        DeleteOperation op = new DeleteOperation(userId, timestamp, nodeId);
        op.apply(localDocument);
        undoStack.push(op);
        redoStack.clear();

        sendOperation(op); // Send via WebSocket
        return op;
    }

    public void applyRemoteOperation(CRDTOperation operation) {
        operation.apply(localDocument);
    }

    private void sendOperation(CRDTOperation operation) {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.send("/app/collab", serializer.serialize(operation));
        }
    }

    // Undo/Redo methods remain identical to original
    public boolean undo() {
        if (undoStack.isEmpty())
            return false;

        CRDTOperation op = undoStack.pop();
        reverseOperation(op).apply(localDocument);
        redoStack.push(op);
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty())
            return false;

        CRDTOperation op = redoStack.pop();
        op.apply(localDocument);
        undoStack.push(op);
        return true;
    }

    private CRDTOperation reverseOperation(CRDTOperation op) {
        if (op instanceof InsertOperation) {
            InsertOperation insertOp = (InsertOperation) op;
            UUID nodeId = localDocument.findNodeAtPosition(
                    localDocument.getText().indexOf(insertOp.getValue()));
            return new DeleteOperation(userId, localClock.incrementAndGet(), nodeId);
        } else if (op instanceof DeleteOperation) {
            DeleteOperation deleteOp = (DeleteOperation) op;
            CRDTNode node = localDocument.getAllNodes().stream()
                    .filter(n -> n.getId().equals(deleteOp.getNodeId()))
                    .findFirst()
                    .orElse(null);
            if (node != null) {
                return new InsertOperation(
                        userId,
                        localClock.incrementAndGet(),
                        node.getValue(),
                        node.getParentId());
            }
        }
        return null;
    }

    public String getText() {
        return localDocument.getText();
    }
}