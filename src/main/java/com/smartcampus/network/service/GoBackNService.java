package com.smartcampus.network.service;

import com.smartcampus.network.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoBackNService {

    public GoBackNResponse simulate(GoBackNRequest request) {
        if (request == null) {
            return GoBackNResponse.error("Request cannot be null.");
        }

        int total = request.getTotalFrames();
        int windowSize = request.getWindowSize();
        int lostFrame = request.getLostFrame();

        if (total < 1 || total > 20) {
            return GoBackNResponse.error("Total frames must be between 1 and 20.");
        }
        if (windowSize < 1 || windowSize > 7) {
            return GoBackNResponse.error("Window size must be between 1 and 7.");
        }

        List<GoBackNStep> steps = new ArrayList<>();
        Map<Integer, String> senderState = new HashMap<>();
        Map<Integer, String> receiverState = new HashMap<>();

        for (int i = 1; i <= total; i++) {
            senderState.put(i, "PENDING");
            receiverState.put(i, "WAITING");
        }

        int windowStart = 1;
        int windowEnd = Math.min(windowSize, total);
        int receiverExpected = 1;
        int stepNum = 1;
        int totalTransmissions = 0;
        int retransmissionCount = 0;
        boolean lossSimulated = false;

        // Step 0: Initialization
        GoBackNStep init = new GoBackNStep();
        init.setStepNumber(0);
        init.setEventType("INITIALIZE");
        init.setFrameId(0);
        init.setSenderMessage("GBN Sender initialized with Window Size " + windowSize + " (Window [" + windowStart + " .. " + windowEnd + "]).");
        init.setReceiverMessage("GBN Receiver initialized. Expecting Frame 1.");
        init.setWindowStart(windowStart);
        init.setWindowEnd(windowEnd);
        init.setExpectedFrameReceiver(receiverExpected);
        init.setSenderFrameState(new HashMap<>(senderState));
        init.setReceiverFrameState(new HashMap<>(receiverState));
        init.setCurrentWindow(getWindowList(windowStart, windowEnd));
        steps.add(init);

        // Simulation loop
        while (receiverExpected <= total) {
            // Determine frames to send in the current window
            int sendStart = windowStart;
            int sendEnd = windowEnd;

            // Transmit frames in window
            for (int f = sendStart; f <= sendEnd; f++) {
                if (f < receiverExpected) continue; // Already acked

                totalTransmissions++;

                if (f == lostFrame && !lossSimulated) {
                    // Frame Loss Event
                    lossSimulated = true;
                    senderState.put(f, "LOST");

                    GoBackNStep lossStep = new GoBackNStep();
                    lossStep.setStepNumber(stepNum++);
                    lossStep.setEventType("LOSS");
                    lossStep.setFrameId(f);
                    lossStep.setSenderMessage("Sender transmits Frame " + f + ", but packet is LOST in transit due to channel noise!");
                    lossStep.setReceiverMessage("Receiver still waiting for Frame " + receiverExpected + ". Packet never arrived.");
                    lossStep.setWindowStart(windowStart);
                    lossStep.setWindowEnd(windowEnd);
                    lossStep.setExpectedFrameReceiver(receiverExpected);
                    lossStep.setSenderFrameState(new HashMap<>(senderState));
                    lossStep.setReceiverFrameState(new HashMap<>(receiverState));
                    lossStep.setCurrentWindow(getWindowList(windowStart, windowEnd));
                    steps.add(lossStep);

                    // Transmit remaining frames in the window that were in flight
                    for (int outOfOrder = f + 1; outOfOrder <= sendEnd; outOfOrder++) {
                        totalTransmissions++;
                        senderState.put(outOfOrder, "SENT");

                        GoBackNStep discardStep = new GoBackNStep();
                        discardStep.setStepNumber(stepNum++);
                        discardStep.setEventType("DISCARD");
                        discardStep.setFrameId(outOfOrder);
                        discardStep.setSenderMessage("Sender transmits Frame " + outOfOrder + " (in flight within current window).");
                        receiverState.put(outOfOrder, "DISCARDED");
                        discardStep.setReceiverMessage("Receiver DISCARDS Frame " + outOfOrder + "! Reason: Out-of-order arrival (expected Frame " + receiverExpected + "). GBN does not buffer out-of-order frames.");
                        discardStep.setWindowStart(windowStart);
                        discardStep.setWindowEnd(windowEnd);
                        discardStep.setExpectedFrameReceiver(receiverExpected);
                        discardStep.setSenderFrameState(new HashMap<>(senderState));
                        discardStep.setReceiverFrameState(new HashMap<>(receiverState));
                        discardStep.setCurrentWindow(getWindowList(windowStart, windowEnd));
                        steps.add(discardStep);
                    }

                    // Sender Timeout Event
                    GoBackNStep timeoutStep = new GoBackNStep();
                    timeoutStep.setStepNumber(stepNum++);
                    timeoutStep.setEventType("TIMEOUT");
                    timeoutStep.setFrameId(f);
                    timeoutStep.setSenderMessage("TIMEOUT! Timer expired for unacknowledged Frame " + f + ". No cumulative ACK received.");
                    timeoutStep.setReceiverMessage("Receiver state unchanged. Cumulative ACK remains ACK " + (receiverExpected - 1) + ".");
                    timeoutStep.setWindowStart(windowStart);
                    timeoutStep.setWindowEnd(windowEnd);
                    timeoutStep.setExpectedFrameReceiver(receiverExpected);
                    timeoutStep.setSenderFrameState(new HashMap<>(senderState));
                    timeoutStep.setReceiverFrameState(new HashMap<>(receiverState));
                    timeoutStep.setCurrentWindow(getWindowList(windowStart, windowEnd));
                    steps.add(timeoutStep);

                    // Go Back N Retransmission: retransmit from lost frame up to current window end
                    for (int r = f; r <= sendEnd; r++) {
                        totalTransmissions++;
                        retransmissionCount++;
                        senderState.put(r, "RETRANSMITTED");

                        GoBackNStep retxStep = new GoBackNStep();
                        retxStep.setStepNumber(stepNum++);
                        retxStep.setEventType("RETRANSMIT");
                        retxStep.setFrameId(r);
                        retxStep.setSenderMessage("Go-Back-N Retransmission: Sender re-sends Frame " + r + ".");

                        if (r == receiverExpected) {
                            receiverState.put(r, "ACCEPTED");
                            receiverExpected++;
                            senderState.put(r, "ACKED");
                            retxStep.setReceiverMessage("Receiver accepts retransmitted Frame " + r + " in-order! Sends cumulative ACK " + r + ".");
                        } else {
                            receiverState.put(r, "DISCARDED");
                            retxStep.setReceiverMessage("Receiver waiting for Frame " + receiverExpected + ".");
                        }

                        // Slide window if windowStart was acknowledged
                        if (r >= windowStart) {
                            windowStart = receiverExpected;
                            windowEnd = Math.min(windowStart + windowSize - 1, total);
                        }

                        retxStep.setWindowStart(Math.min(windowStart, total));
                        retxStep.setWindowEnd(windowEnd);
                        retxStep.setExpectedFrameReceiver(receiverExpected);
                        retxStep.setSenderFrameState(new HashMap<>(senderState));
                        retxStep.setReceiverFrameState(new HashMap<>(receiverState));
                        retxStep.setCurrentWindow(getWindowList(windowStart, windowEnd));
                        steps.add(retxStep);
                    }
                    break; // Break out of inner transmit loop to handle updated window
                } else {
                    // Normal transmission without loss
                    senderState.put(f, "SENT");

                    GoBackNStep sendStep = new GoBackNStep();
                    sendStep.setStepNumber(stepNum++);
                    sendStep.setEventType("TRANSMIT");
                    sendStep.setFrameId(f);
                    sendStep.setSenderMessage("Sender transmits Frame " + f + ".");

                    if (f == receiverExpected) {
                        receiverState.put(f, "ACCEPTED");
                        receiverExpected++;
                        senderState.put(f, "ACKED");
                        sendStep.setReceiverMessage("Receiver receives Frame " + f + " in-order -> Frame ACCEPTED. Cumulative ACK " + f + " sent.");
                    } else {
                        receiverState.put(f, "DISCARDED");
                        sendStep.setReceiverMessage("Receiver discards out-of-order Frame " + f + ".");
                    }

                    // Advance window as ACKs arrive
                    windowStart = receiverExpected;
                    windowEnd = Math.min(windowStart + windowSize - 1, total);

                    sendStep.setWindowStart(Math.min(windowStart, total));
                    sendStep.setWindowEnd(windowEnd);
                    sendStep.setExpectedFrameReceiver(receiverExpected);
                    sendStep.setSenderFrameState(new HashMap<>(senderState));
                    sendStep.setReceiverFrameState(new HashMap<>(receiverState));
                    sendStep.setCurrentWindow(getWindowList(windowStart, windowEnd));
                    steps.add(sendStep);
                }
            }
        }

        // Completion step
        GoBackNStep completeStep = new GoBackNStep();
        completeStep.setStepNumber(stepNum++);
        completeStep.setEventType("COMPLETE");
        completeStep.setFrameId(total);
        completeStep.setSenderMessage("All " + total + " frames successfully transmitted and acknowledged.");
        completeStep.setReceiverMessage("All " + total + " frames received in correct sequence order.");
        completeStep.setWindowStart(total);
        completeStep.setWindowEnd(total);
        completeStep.setExpectedFrameReceiver(total + 1);
        completeStep.setSenderFrameState(new HashMap<>(senderState));
        completeStep.setReceiverFrameState(new HashMap<>(receiverState));
        completeStep.setCurrentWindow(Collections.emptyList());
        steps.add(completeStep);

        GoBackNResponse response = new GoBackNResponse();
        response.setSuccess(true);
        response.setMessage("Go-Back-N ARQ simulation completed with " + retransmissionCount + " retransmitted frame(s).");
        response.setTotalFrames(total);
        response.setWindowSize(windowSize);
        response.setLostFrame(lostFrame);
        response.setTotalTransmissions(totalTransmissions);
        response.setRetransmissionCount(retransmissionCount);
        response.setSteps(steps);
        response.setSummary("Go-Back-N ARQ successfully handled loss of Frame " + lostFrame + ". Out-of-order frames were discarded by receiver, sender timed out, and Go-Back-N window retransmission recovered all lost/unacknowledged data.");

        return response;
    }

    private List<Integer> getWindowList(int start, int end) {
        List<Integer> list = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            list.add(i);
        }
        return list;
    }
}
