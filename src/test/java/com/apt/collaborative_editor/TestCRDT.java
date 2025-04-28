package com.apt.collaborative_editor;

import com.apt.collaborative_editor.Model.TextCRDT;

public class TestCRDT {
    public static void main(String[] args) {
        TextCRDT doc = new TextCRDT("user1");
        doc.insert(0, 'a', "user1");
        doc.insert(1, 'b', "user1");
        doc.insert(2, 'c', "user1");

        doc.delete(1); // Deletes 'b' (now "a c")
        System.out.println(doc.toString()); // Output: "ac"
    }
}