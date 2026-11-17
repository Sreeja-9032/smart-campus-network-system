package com.smartcampus.network.service;

import com.smartcampus.network.model.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FramingService {

    public FramingResponse encodeMessage(FramingRequest request) {
        if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return FramingResponse.error("Message to frame must not be empty.");
        }

        String message = request.getMessage();
        String flag = (request.getFlagDelimiter() == null || request.getFlagDelimiter().trim().isEmpty()) ? "FLAG" : request.getFlagDelimiter().trim();
        String esc = (request.getEscDelimiter() == null || request.getEscDelimiter().trim().isEmpty()) ? "ESC" : request.getEscDelimiter().trim();
        int frameSize = request.getFrameSize() <= 0 ? 10 : request.getFrameSize();

        // 1. Binary representation of original message
        StringBuilder binaryRep = new StringBuilder();
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            binaryRep.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0')).append(" ");
        }

        // 2. Divide original message into frame-sized payloads
        List<String> rawChunks = new ArrayList<>();
        for (int i = 0; i < message.length(); i += frameSize) {
            rawChunks.add(message.substring(i, Math.min(i + frameSize, message.length())));
        }

        // 3. Byte stuff each frame
        List<FrameItem> frameItems = new ArrayList<>();
        int index = 1;
        for (String chunk : rawChunks) {
            FrameItem item = stuffPayload(chunk, index++, flag, esc);
            frameItems.add(item);
        }

        FramingResponse response = new FramingResponse();
        response.setSuccess(true);
        response.setMessage("Message successfully fragmented and byte-stuffed into " + frameItems.size() + " frame(s).");
        response.setOriginalMessage(message);
        response.setBinaryRepresentation(binaryRep.toString().trim());
        response.setFlagDelimiter(flag);
        response.setEscDelimiter(esc);
        response.setFrameSize(frameSize);
        response.setTotalFrames(frameItems.size());
        response.setFrames(frameItems);

        return response;
    }

    private FrameItem stuffPayload(String rawPayload, int frameIndex, String flag, String esc) {
        StringBuilder rawHex = new StringBuilder();
        for (char c : rawPayload.toCharArray()) {
            rawHex.append(String.format("%02X ", (int) c));
        }

        List<String> transformations = new ArrayList<>();
        StringBuilder stuffed = new StringBuilder();
        int flagEscapes = 0;
        int escEscapes = 0;

        int i = 0;
        while (i < rawPayload.length()) {
            if (rawPayload.startsWith(flag, i)) {
                stuffed.append(esc).append(flag);
                transformations.add("Pattern '" + flag + "' escaped to '" + esc + flag + "'");
                flagEscapes++;
                i += flag.length();
            } else if (rawPayload.startsWith(esc, i)) {
                stuffed.append(esc).append(esc);
                transformations.add("Escape char '" + esc + "' escaped to '" + esc + esc + "'");
                escEscapes++;
                i += esc.length();
            } else {
                stuffed.append(rawPayload.charAt(i));
                i++;
            }
        }

        String stuffedPayloadStr = stuffed.toString();
        String framedData = flag + " [ " + stuffedPayloadStr + " ] " + flag;

        return new FrameItem(
                frameIndex,
                rawPayload,
                rawHex.toString().trim(),
                stuffedPayloadStr,
                framedData,
                flagEscapes,
                escEscapes,
                transformations
        );
    }

    public DeframingResponse decodeFrames(DeframingRequest request) {
        if (request == null || request.getFramedDataList() == null || request.getFramedDataList().isEmpty()) {
            return DeframingResponse.error("No framed data provided for decoding.");
        }

        String flag = (request.getFlagDelimiter() == null || request.getFlagDelimiter().trim().isEmpty()) ? "FLAG" : request.getFlagDelimiter().trim();
        String esc = (request.getEscDelimiter() == null || request.getEscDelimiter().trim().isEmpty()) ? "ESC" : request.getEscDelimiter().trim();

        StringBuilder reconstructed = new StringBuilder();
        List<String> destuffedPayloads = new ArrayList<>();

        int processed = 0;
        for (String framedData : request.getFramedDataList()) {
            if (framedData == null || framedData.trim().isEmpty()) continue;

            String payload = framedData;
            String prefix = flag + " [ ";
            String suffix = " ] " + flag;
            if (payload.startsWith(prefix) && payload.endsWith(suffix)) {
                payload = payload.substring(prefix.length(), payload.length() - suffix.length());
            } else {
                if (payload.startsWith(flag)) {
                    payload = payload.substring(flag.length());
                }
                if (payload.endsWith(flag)) {
                    payload = payload.substring(0, payload.length() - flag.length());
                }
                payload = payload.trim();
                if (payload.startsWith("[") && payload.endsWith("]")) {
                    payload = payload.substring(1, payload.length() - 1);
                }
            }

            // De-stuffing process: Replace ESC+FLAG -> FLAG, and ESC+ESC -> ESC
            StringBuilder destuffed = new StringBuilder();
            String escapeFlagPattern = esc + flag;
            String escapeEscPattern = esc + esc;

            int i = 0;
            while (i < payload.length()) {
                if (payload.startsWith(escapeFlagPattern, i)) {
                    destuffed.append(flag);
                    i += escapeFlagPattern.length();
                } else if (payload.startsWith(escapeEscPattern, i)) {
                    destuffed.append(esc);
                    i += escapeEscPattern.length();
                } else {
                    destuffed.append(payload.charAt(i));
                    i++;
                }
            }

            String destuffedStr = destuffed.toString();
            destuffedPayloads.add(destuffedStr);
            reconstructed.append(destuffedStr);
            processed++;
        }

        DeframingResponse response = new DeframingResponse();
        response.setSuccess(true);
        response.setMessage("Successfully de-stuffed and reassembled " + processed + " frame(s).");
        response.setReconstructedMessage(reconstructed.toString());
        response.setFramesProcessed(processed);
        response.setDestuffedPayloads(destuffedPayloads);

        return response;
    }
}
