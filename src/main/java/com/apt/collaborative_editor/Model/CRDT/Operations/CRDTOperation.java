package com.apt.collaborative_editor.Model.CRDT.Operations;

import java.util.UUID;

import com.apt.collaborative_editor.Model.CRDT.CRDTDocument;

public interface CRDTOperation {
    void apply(CRDTDocument document);

    UUID getUserId();

    long getTimestamp();
}