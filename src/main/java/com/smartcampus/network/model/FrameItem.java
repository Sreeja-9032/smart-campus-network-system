package com.smartcampus.network.model;

import java.util.List;

public class FrameItem {
    private int frameIndex;
    private String rawPayload;
    private String rawHexOrBinary;
    private String stuffedPayload;
    private String framedData;
    private int escapedFlagCount;
    private int escapedEscCount;
    private List<String> transformations;

    public FrameItem() {}

    public FrameItem(int frameIndex, String rawPayload, String rawHexOrBinary, String stuffedPayload, String framedData, int escapedFlagCount, int escapedEscCount, List<String> transformations) {
        this.frameIndex = frameIndex;
        this.rawPayload = rawPayload;
        this.rawHexOrBinary = rawHexOrBinary;
        this.stuffedPayload = stuffedPayload;
        this.framedData = framedData;
        this.escapedFlagCount = escapedFlagCount;
        this.escapedEscCount = escapedEscCount;
        this.transformations = transformations;
    }

    public int getFrameIndex() { return frameIndex; }
    public void setFrameIndex(int frameIndex) { this.frameIndex = frameIndex; }

    public String getRawPayload() { return rawPayload; }
    public void setRawPayload(String rawPayload) { this.rawPayload = rawPayload; }

    public String getRawHexOrBinary() { return rawHexOrBinary; }
    public void setRawHexOrBinary(String rawHexOrBinary) { this.rawHexOrBinary = rawHexOrBinary; }

    public String getStuffedPayload() { return stuffedPayload; }
    public void setStuffedPayload(String stuffedPayload) { this.stuffedPayload = stuffedPayload; }

    public String getFramedData() { return framedData; }
    public void setFramedData(String framedData) { this.framedData = framedData; }

    public int getEscapedFlagCount() { return escapedFlagCount; }
    public void setEscapedFlagCount(int escapedFlagCount) { this.escapedFlagCount = escapedFlagCount; }

    public int getEscapedEscCount() { return escapedEscCount; }
    public void setEscapedEscCount(int escapedEscCount) { this.escapedEscCount = escapedEscCount; }

    public List<String> getTransformations() { return transformations; }
    public void setTransformations(List<String> transformations) { this.transformations = transformations; }
}
