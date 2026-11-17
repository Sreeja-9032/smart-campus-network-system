package com.smartcampus.network.model;

import java.util.List;

public class DeframingRequest {
    private List<String> framedDataList;
    private String flagDelimiter;
    private String escDelimiter;

    public DeframingRequest() {
        this.flagDelimiter = "FLAG";
        this.escDelimiter = "ESC";
    }

    public DeframingRequest(List<String> framedDataList, String flagDelimiter, String escDelimiter) {
        this.framedDataList = framedDataList;
        this.flagDelimiter = flagDelimiter;
        this.escDelimiter = escDelimiter;
    }

    public List<String> getFramedDataList() { return framedDataList; }
    public void setFramedDataList(List<String> framedDataList) { this.framedDataList = framedDataList; }

    public String getFlagDelimiter() { return flagDelimiter; }
    public void setFlagDelimiter(String flagDelimiter) { this.flagDelimiter = flagDelimiter; }

    public String getEscDelimiter() { return escDelimiter; }
    public void setEscDelimiter(String escDelimiter) { this.escDelimiter = escDelimiter; }
}
