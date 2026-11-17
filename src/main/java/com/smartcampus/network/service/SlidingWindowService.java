package com.smartcampus.network.service;

import com.smartcampus.network.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SlidingWindowService {

    public SlidingWindowResponse simulate(SlidingWindowRequest request) {
        if (request == null) {
            return SlidingWindowResponse.error("Request cannot be null.");
        }

        int total = request.getTotalFrames();
        int windowSize = request.getWindowSize();

        if (total < 1 || total > 20) {
            return SlidingWindowResponse.error("Total frames must be between 1 and 20.");
        }
        if (windowSize < 1 || windowSize > 7) {
            return SlidingWindowResponse.error("Window size must be between 1 and 7.");
        }

        List<SlidingWindowStep> steps = new ArrayList<>();
        Map<Integer, String> frameStatus = new HashMap<>();
        for (int i = 1; i <= total; i++) {
            frameStatus.put(i, "PENDING");
        }

        int windowStart = 1;
        int windowEnd = Math.min(windowSize, total);
        int nextToSend = 1;
        int nextExpectedAck = 1;

        // Step 0: Initial state
        for (int i = windowStart; i <= windowEnd; i++) {
            frameStatus.put(i, "IN_WINDOW");
        }

        SlidingWindowStep initStep = new SlidingWindowStep();
        initStep.setStepNumber(0);
        initStep.setAction("INITIALIZE");
        initStep.setDescription("Sender initialized with Window Size " + windowSize + ". Current Window: [" + windowStart + " .. " + windowEnd + "].");
        initStep.setWindowStart(windowStart);
        initStep.setWindowEnd(windowEnd);
        initStep.setCurrentFrame(1);
        initStep.setAcknowledgedFrame(0);
        initStep.setFramesRemaining(total);
        initStep.setFrameStatuses(new HashMap<>(frameStatus));
        initStep.setCurrentWindowFrames(getWindowList(windowStart, windowEnd));
        steps.add(initStep);

        int stepNum = 1;

        // Simulate frame-by-frame transmission and window sliding
        while (nextExpectedAck <= total) {
            // If there are un-sent frames in current window, send them
            if (nextToSend <= windowEnd) {
                int frame = nextToSend;
                frameStatus.put(frame, "TRANSMITTING");

                SlidingWindowStep sendStep = new SlidingWindowStep();
                sendStep.setStepNumber(stepNum++);
                sendStep.setAction("TRANSMIT_FRAME");
                sendStep.setDescription("Sender transmits Frame " + frame + " across the network to Receiver.");
                sendStep.setWindowStart(windowStart);
                sendStep.setWindowEnd(windowEnd);
                sendStep.setCurrentFrame(frame);
                sendStep.setAcknowledgedFrame(nextExpectedAck - 1);
                sendStep.setFramesRemaining(total - nextExpectedAck + 1);
                sendStep.setFrameStatuses(new HashMap<>(frameStatus));
                sendStep.setCurrentWindowFrames(getWindowList(windowStart, windowEnd));
                steps.add(sendStep);

                nextToSend++;
            } else {
                // All frames in current window are transmitted; receive ACK and slide window
                int ackFrame = nextExpectedAck;
                frameStatus.put(ackFrame, "ACKNOWLEDGED");

                SlidingWindowStep ackStep = new SlidingWindowStep();
                ackStep.setStepNumber(stepNum++);
                ackStep.setAction("RECEIVE_ACK");
                ackStep.setDescription("Receiver receives Frame " + ackFrame + " in-order and sends back ACK " + ackFrame + ".");
                ackStep.setWindowStart(windowStart);
                ackStep.setWindowEnd(windowEnd);
                ackStep.setCurrentFrame(nextToSend - 1);
                ackStep.setAcknowledgedFrame(ackFrame);
                ackStep.setFramesRemaining(total - ackFrame);
                ackStep.setFrameStatuses(new HashMap<>(frameStatus));
                ackStep.setCurrentWindowFrames(getWindowList(windowStart, windowEnd));
                steps.add(ackStep);

                // Slide window forward by 1
                windowStart++;
                windowEnd = Math.min(windowStart + windowSize - 1, total);
                nextExpectedAck++;

                if (windowEnd >= nextToSend && nextToSend <= total) {
                    for (int f = nextToSend; f <= windowEnd; f++) {
                        if ("PENDING".equals(frameStatus.get(f))) {
                            frameStatus.put(f, "IN_WINDOW");
                        }
                    }
                }

                SlidingWindowStep slideStep = new SlidingWindowStep();
                slideStep.setStepNumber(stepNum++);
                slideStep.setAction("SLIDE_WINDOW");
                if (windowStart <= total) {
                    slideStep.setDescription("Window slides forward: new window is [" + windowStart + " .. " + windowEnd + "]. Frame " + ackFrame + " acknowledged and discarded from sender buffer.");
                } else {
                    slideStep.setDescription("All " + total + " frames successfully transmitted, acknowledged, and buffer emptied.");
                }
                slideStep.setWindowStart(Math.min(windowStart, total));
                slideStep.setWindowEnd(windowEnd);
                slideStep.setCurrentFrame(Math.min(nextToSend, total));
                slideStep.setAcknowledgedFrame(ackFrame);
                slideStep.setFramesRemaining(total - ackFrame);
                slideStep.setFrameStatuses(new HashMap<>(frameStatus));
                slideStep.setCurrentWindowFrames(getWindowList(windowStart, windowEnd));
                steps.add(slideStep);
            }
        }

        SlidingWindowResponse response = new SlidingWindowResponse();
        response.setSuccess(true);
        response.setMessage("Sliding window simulation completed successfully for " + total + " frames.");
        response.setTotalFrames(total);
        response.setWindowSize(windowSize);
        response.setTotalSteps(steps.size());
        response.setSteps(steps);

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
