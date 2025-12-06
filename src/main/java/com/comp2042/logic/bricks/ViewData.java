package com.comp2042;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int ghostYPosition;
    private final java.util.List<int[][]> nextBrickData;
    private final int[][] holdBrickData;

    public ViewData(int[][] brickData, int xPosition, int yPosition, int ghostYPosition,
            java.util.List<int[][]> nextBrickData,
            int[][] holdBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.ghostYPosition = ghostYPosition;
        this.nextBrickData = nextBrickData;
        this.holdBrickData = holdBrickData;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int getGhostYPosition() {
        return ghostYPosition;
    }

    public java.util.List<int[][]> getNextBrickData() {
        return nextBrickData;
    }

    public int[][] getHoldBrickData() {
        return holdBrickData == null ? null : MatrixOperations.copy(holdBrickData);
    }
}
