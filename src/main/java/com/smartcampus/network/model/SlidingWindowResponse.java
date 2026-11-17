package com.smartcampus.network.model;

import java.util.List;

public class SlidingWindowResponse {
    private boolean success;
    private String message;
    private int totalFrames;
    private int windowSize;
    private int totalSteps;
    private List<SlidingWindowStep> steps;

    public SlidingWindowResponse() {}

    public static SlidingWindowResponse error(String message) {
        SlidingWindowResponse resp = new SlidingWindowResponse();
        resp.setSuccess(false);
        resp.setMessage(message);
        return resp;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getTotalFrames() { return totalFrames; }
    public void setTotalFrames(int totalFrames) { this.totalFrames = totalFrames; }

    public int getWindowSize() { return windowSize; }
    public void setWindowSize(int windowSize) { this.windowSize = windowSize; }

    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }

    public List<SlidingWindowStep> getSteps() { return steps; }
    public void setSteps(List<SlidingWindowStep> steps) { this.steps = steps; }
}
