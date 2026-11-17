package com.smartcampus.network.model;

import java.util.List;
import java.util.Map;

public class GoBackNStep {
    private int stepNumber;
    private String eventType; // TRANSMIT, LOSS, DISCARD, ACK, TIMEOUT, RETRANSMIT, COMPLETE
    private int frameId;
    private String senderMessage;
    private String receiverMessage;
    private int windowStart;
    private int windowEnd;
    private int expectedFrameReceiver;
    private Map<Integer, String> senderFrameState; // PENDING, SENT, ACKED, LOST, RETRANSMITTED
    private Map<Integer, String> receiverFrameState; // WAITING, ACCEPTED, DISCARDED
    private List<Integer> currentWindow;

    public GoBackNStep() {}

    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public int getFrameId() { return frameId; }
    public void setFrameId(int frameId) { this.frameId = frameId; }

    public String getSenderMessage() { return senderMessage; }
    public void setSenderMessage(String senderMessage) { this.senderMessage = senderMessage; }

    public String getReceiverMessage() { return receiverMessage; }
    public void setReceiverMessage(String receiverMessage) { this.receiverMessage = receiverMessage; }

    public int getWindowStart() { return windowStart; }
    public void setWindowStart(int windowStart) { this.windowStart = windowStart; }

    public int getWindowEnd() { return windowEnd; }
    public void setWindowEnd(int windowEnd) { this.windowEnd = windowEnd; }

    public int getExpectedFrameReceiver() { return expectedFrameReceiver; }
    public void setExpectedFrameReceiver(int expectedFrameReceiver) { this.expectedFrameReceiver = expectedFrameReceiver; }

    public Map<Integer, String> getSenderFrameState() { return senderFrameState; }
    public void setSenderFrameState(Map<Integer, String> senderFrameState) { this.senderFrameState = senderFrameState; }

    public Map<Integer, String> getReceiverFrameState() { return receiverFrameState; }
    public void setReceiverFrameState(Map<Integer, String> receiverFrameState) { this.receiverFrameState = receiverFrameState; }

    public List<Integer> getCurrentWindow() { return currentWindow; }
    public void setCurrentWindow(List<Integer> currentWindow) { this.currentWindow = currentWindow; }
}
