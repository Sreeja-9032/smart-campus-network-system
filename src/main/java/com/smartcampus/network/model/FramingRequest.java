package com.smartcampus.network.model;

public class FramingRequest {
    private String message;
    private String flagDelimiter;
    private String escDelimiter;
    private int frameSize;

    public FramingRequest() {
        this.flagDelimiter = "FLAG";
        this.escDelimiter = "ESC";
        this.frameSize = 10;
    }

    public FramingRequest(String message, String flagDelimiter, String escDelimiter, int frameSize) {
        this.message = message;
        this.flagDelimiter = flagDelimiter;
        this.escDelimiter = escDelimiter;
        this.frameSize = frameSize;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getFlagDelimiter() { return flagDelimiter; }
    public void setFlagDelimiter(String flagDelimiter) { this.flagDelimiter = flagDelimiter; }

    public String getEscDelimiter() { return escDelimiter; }
    public void setEscDelimiter(String escDelimiter) { this.escDelimiter = escDelimiter; }

    public int getFrameSize() { return frameSize; }
    public void setFrameSize(int frameSize) { this.frameSize = frameSize; }
}
