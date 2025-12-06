package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick holdBrick;
    private boolean canHold = true;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(),
                (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(),
                (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(),
                (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    // ✅ IMPLEMENT rotateBrick to satisfy the interface
    @Override
    public boolean rotateBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(),
                (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    @Override
    public boolean holdBrick() {
        if (!canHold) {
            return false;
        }

        Brick currentBrick = brickRotator.getBrick();
        if (holdBrick == null) {
            holdBrick = currentBrick;
            createNewBrick();
            // Don't reset canHold here because createNewBrick sets it to true, but we want
            // it false for THIS turn
        } else {
            Brick temp = holdBrick;
            holdBrick = currentBrick;
            brickRotator.setBrick(temp);
            currentOffset = new Point(4, 0);
        }

        canHold = false;
        return true;
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 0);
        canHold = true;
        return !MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(),
                (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        java.util.List<int[][]> nextShapes = new java.util.ArrayList<>();
        for (Brick b : brickGenerator.getNextBricks()) {
            nextShapes.add(b.getShapeMatrix().get(0));
        }

        return new ViewData(
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                getGhostY(),
                nextShapes,
                holdBrick == null ? null : holdBrick.getShapeMatrix().get(0));
    }

    private int getGhostY() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int ghostY = (int) currentOffset.getY();
        while (true) {
            boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(),
                    (int) currentOffset.getX(), ghostY + 1);
            if (conflict) {
                break;
            }
            ghostY++;
        }
        return ghostY;
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(),
                (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        holdBrick = null;
        createNewBrick();
    }
}
