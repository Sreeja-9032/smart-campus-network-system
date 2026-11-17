package com.smartcampus.network.model;

import java.util.List;
import java.util.Map;

public class SlidingWindowStep {
    private int stepNumber;
    private String action;
    private String description;
    private int windowStart;
    private int windowEnd;
    private int currentFrame;
    private int acknowledgedFrame;
    private int framesRemaining;
    private Map<Integer, String> frameStatuses; // PENDING, IN_WINDOW, TRANSMITTING, ACKNOWLEDGED
    private List<Integer> currentWindowFrames;

    public SlidingWindowStep() {}

    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getWindowStart() { return windowStart; }
    public void setWindowStart(int windowStart) { this.windowStart = windowStart; }

    public int getWindowEnd() { return windowEnd; }
    public void setWindowEnd(int windowEnd) { this.windowEnd = windowEnd; }

    public int getCurrentFrame() { return currentFrame; }
    public void setCurrentFrame(int currentFrame) { this.currentFrame = currentFrame; }

    public int getAcknowledgedFrame() { return acknowledgedFrame; }
    public void setAcknowledgedFrame(int acknowledgedFrame) { this.acknowledgedFrame = acknowledgedFrame; }

    public int getFramesRemaining() { return framesRemaining; }
    public void setFramesRemaining(int framesRemaining) { this.framesRemaining = framesRemaining; }

    public Map<Integer, String> getFrameStatuses() { return frameStatuses; }
    public void setFrameStatuses(Map<Integer, String> frameStatuses) { this.frameStatuses = frameStatuses; }

    public List<Integer> getCurrentWindowFrames() { return currentWindowFrames; }
    public void setCurrentWindowFrames(List<Integer> currentWindowFrames) { this.currentWindowFrames = currentWindowFrames; }
}
