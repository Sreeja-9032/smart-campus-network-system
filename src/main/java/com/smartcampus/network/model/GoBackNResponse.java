package com.smartcampus.network.model;

import java.util.List;

public class GoBackNResponse {
    private boolean success;
    private String message;
    private int totalFrames;
    private int windowSize;
    private int lostFrame;
    private int totalTransmissions;
    private int retransmissionCount;
    private List<GoBackNStep> steps;
    private String summary;

    public GoBackNResponse() {}

    public static GoBackNResponse error(String message) {
        GoBackNResponse resp = new GoBackNResponse();
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

    public int getLostFrame() { return lostFrame; }
    public void setLostFrame(int lostFrame) { this.lostFrame = lostFrame; }

    public int getTotalTransmissions() { return totalTransmissions; }
    public void setTotalTransmissions(int totalTransmissions) { this.totalTransmissions = totalTransmissions; }

    public int getRetransmissionCount() { return retransmissionCount; }
    public void setRetransmissionCount(int retransmissionCount) { this.retransmissionCount = retransmissionCount; }

    public List<GoBackNStep> getSteps() { return steps; }
    public void setSteps(List<GoBackNStep> steps) { this.steps = steps; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
