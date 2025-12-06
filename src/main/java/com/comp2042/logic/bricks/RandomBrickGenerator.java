package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        // Initialize with enough bricks
        while (nextBricks.size() < 4) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
    }

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 3) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        return nextBricks.poll();
    }

    @Override
    public List<Brick> getNextBricks() {
        // Ensure we always have enough future bricks to show 3
        while (nextBricks.size() <= 3) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }

        List<Brick> preview = new ArrayList<>();
        // Peek at the first 3 without removing
        // Since ArrayDeque iteration order is head-to-tail, we can just iterate
        int count = 0;
        for (Brick b : nextBricks) {
            preview.add(b);
            count++;
            if (count >= 3)
                break;
        }
        return preview;
    }
}
