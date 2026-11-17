package com.smartcampus.network.model;

public class CrcVerifyRequest {
    private String receivedCodeword;
    private String polynomial;

    public CrcVerifyRequest() {}

    public CrcVerifyRequest(String receivedCodeword, String polynomial) {
        this.receivedCodeword = receivedCodeword;
        this.polynomial = polynomial;
    }

    public String getReceivedCodeword() { return receivedCodeword; }
    public void setReceivedCodeword(String receivedCodeword) { this.receivedCodeword = receivedCodeword; }

    public String getPolynomial() { return polynomial; }
    public void setPolynomial(String polynomial) { this.polynomial = polynomial; }
}
