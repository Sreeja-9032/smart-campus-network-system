package com.smartcampus.network.model;

public class GoBackNRequest {
    private int totalFrames;
    private int windowSize;
    private int lostFrame; // 1-based index of frame to drop, or -1 for no loss

    public GoBackNRequest() {
        this.totalFrames = 8;
        this.windowSize = 4;
        this.lostFrame = 3;
    }

    public GoBackNRequest(int totalFrames, int windowSize, int lostFrame) {
        this.totalFrames = totalFrames;
        this.windowSize = windowSize;
        this.lostFrame = lostFrame;
    }

    public int getTotalFrames() { return totalFrames; }
    public void setTotalFrames(int totalFrames) { this.totalFrames = totalFrames; }

    public int getWindowSize() { return windowSize; }
    public void setWindowSize(int windowSize) { this.windowSize = windowSize; }

    public int getLostFrame() { return lostFrame; }
    public void setLostFrame(int lostFrame) { this.lostFrame = lostFrame; }
}
