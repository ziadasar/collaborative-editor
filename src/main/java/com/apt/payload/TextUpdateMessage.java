package com.apt.payload;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TextUpdateMessage {
    private String docId;
    private String updatedText;
}

