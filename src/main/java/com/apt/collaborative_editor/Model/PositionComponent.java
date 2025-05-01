package com.apt.collaborative_editor.Model;


import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PositionComponent {
    private int digit;
    private String userId;
}
