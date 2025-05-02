package com.apt.collaborative_editor;
import com.apt.collaborative_editor.Model.PositionComponent;
import com.apt.collaborative_editor.Model.PositionIdentifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PositionIdentifierGenerator {
    private static final int BASE = 32; // you can adjust this
    private static final int BOUNDARY = 10;
    private static final Random random = new Random();

    public static PositionIdentifier generateBetween(PositionIdentifier prev, PositionIdentifier next, String userId) {
        List<PositionComponent> newPath = new ArrayList<>();

        int depth = 0;
        while (true) {
            int prevDigit = getDigitAt(prev, depth, 0);
            int nextDigit = getDigitAt(next, depth, BASE);

            if (nextDigit - prevDigit > 1) {
                int newDigit = randomBetween(prevDigit + 1, nextDigit - 1);
                newPath.add(new PositionComponent());
                return new PositionIdentifier(newPath);
            } else {
                newPath.add(new PositionComponent());
                depth++;
            }
        }
    }

    private static int getDigitAt(PositionIdentifier id, int index, int defaultDigit) {
        if (id.getPath().size() > index) {
            return id.getPath().get(index).getDigit();
        }
        return defaultDigit;
    }

    private static int randomBetween(int min, int max) {
        if (min > max) return min;
        return min + random.nextInt(max - min + 1);
    }
}

