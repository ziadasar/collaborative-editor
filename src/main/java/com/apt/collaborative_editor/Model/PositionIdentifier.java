package com.apt.collaborative_editor.Model;

import java.util.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionIdentifier implements Comparable<PositionIdentifier> {
    private List<PositionComponent> path;

    @Override
    public int compareTo(PositionIdentifier other) {
        int minLength = Math.min(this.path.size(), other.path.size());
        for (int i = 0; i < minLength; i++) {
            int cmp = Integer.compare(this.path.get(i).getDigit(), other.path.get(i).getDigit());
            if (cmp != 0) return cmp;

            cmp = this.path.get(i).getUserId().compareTo(other.path.get(i).getUserId());
            if (cmp != 0) return cmp;
        }
        return Integer.compare(this.path.size(), other.path.size());
    }
}
