package com.smartcampus.network.model;

public class Ns2TraceImportRequest {
    private String traceContent;

    public Ns2TraceImportRequest() {}

    public Ns2TraceImportRequest(String traceContent) {
        this.traceContent = traceContent;
    }

    public String getTraceContent() { return traceContent; }
    public void setTraceContent(String traceContent) { this.traceContent = traceContent; }
}
