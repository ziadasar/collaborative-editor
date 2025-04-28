package com.apt.collaborative_editor.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharId implements Comparable<CharId> {
    private final int position; // Logical position (e.g., fractional index between neighbors)
    private final String userId; // Unique user ID (e.g., UUID)
    private final long timestamp; // Lamport timestamp or system clock

    public CharId(int position, String userId, long timestamp) {
        this.position = position;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(CharId other) {
        if (this.position != other.position) {
            return Integer.compare(this.position, other.position);
        } else if (this.timestamp != other.timestamp) {
            return Long.compare(this.timestamp, other.timestamp);
        } else {
            return this.userId.compareTo(other.userId);
        }
    }

    // Getters (omitted for brevity)
}