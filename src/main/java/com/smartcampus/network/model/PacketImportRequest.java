package com.smartcampus.network.model;

import java.util.List;

public class PacketImportRequest {
    private String csvContent;

    public PacketImportRequest() {}

    public PacketImportRequest(String csvContent) {
        this.csvContent = csvContent;
    }

    public String getCsvContent() { return csvContent; }
    public void setCsvContent(String csvContent) { this.csvContent = csvContent; }
}
