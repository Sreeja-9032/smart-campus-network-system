package com.smartcampus.network.model;

import java.util.List;

public class FramingResponse {
    private boolean success;
    private String message;
    private String originalMessage;
    private String binaryRepresentation;
    private String flagDelimiter;
    private String escDelimiter;
    private int frameSize;
    private int totalFrames;
    private List<FrameItem> frames;

    public FramingResponse() {}

    public static FramingResponse error(String message) {
        FramingResponse resp = new FramingResponse();
        resp.setSuccess(false);
        resp.setMessage(message);
        return resp;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getOriginalMessage() { return originalMessage; }
    public void setOriginalMessage(String originalMessage) { this.originalMessage = originalMessage; }

    public String getBinaryRepresentation() { return binaryRepresentation; }
    public void setBinaryRepresentation(String binaryRepresentation) { this.binaryRepresentation = binaryRepresentation; }

    public String getFlagDelimiter() { return flagDelimiter; }
    public void setFlagDelimiter(String flagDelimiter) { this.flagDelimiter = flagDelimiter; }

    public String getEscDelimiter() { return escDelimiter; }
    public void setEscDelimiter(String escDelimiter) { this.escDelimiter = escDelimiter; }

    public int getFrameSize() { return frameSize; }
    public void setFrameSize(int frameSize) { this.frameSize = frameSize; }

    public int getTotalFrames() { return totalFrames; }
    public void setTotalFrames(int totalFrames) { this.totalFrames = totalFrames; }

    public List<FrameItem> getFrames() { return frames; }
    public void setFrames(List<FrameItem> frames) { this.frames = frames; }
}
