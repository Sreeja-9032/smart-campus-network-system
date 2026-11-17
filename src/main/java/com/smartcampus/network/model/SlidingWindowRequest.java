package com.smartcampus.network.model;

public class SlidingWindowRequest {
    private int totalFrames;
    private int windowSize;

    public SlidingWindowRequest() {
        this.totalFrames = 8;
        this.windowSize = 4;
    }

    public SlidingWindowRequest(int totalFrames, int windowSize) {
        this.totalFrames = totalFrames;
        this.windowSize = windowSize;
    }

    public int getTotalFrames() { return totalFrames; }
    public void setTotalFrames(int totalFrames) { this.totalFrames = totalFrames; }

    public int getWindowSize() { return windowSize; }
    public void setWindowSize(int windowSize) { this.windowSize = windowSize; }
}
