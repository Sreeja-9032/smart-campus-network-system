package com.smartcampus.network.model;

import java.util.List;

public class DeframingResponse {
    private boolean success;
    private String message;
    private String reconstructedMessage;
    private int framesProcessed;
    private List<String> destuffedPayloads;

    public DeframingResponse() {}

    public static DeframingResponse error(String message) {
        DeframingResponse resp = new DeframingResponse();
        resp.setSuccess(false);
        resp.setMessage(message);
        return resp;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getReconstructedMessage() { return reconstructedMessage; }
    public void setReconstructedMessage(String reconstructedMessage) { this.reconstructedMessage = reconstructedMessage; }

    public int getFramesProcessed() { return framesProcessed; }
    public void setFramesProcessed(int framesProcessed) { this.framesProcessed = framesProcessed; }

    public List<String> getDestuffedPayloads() { return destuffedPayloads; }
    public void setDestuffedPayloads(List<String> destuffedPayloads) { this.destuffedPayloads = destuffedPayloads; }
}
