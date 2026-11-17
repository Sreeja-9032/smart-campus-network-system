package com.smartcampus.network.model;

public class CrcRequest {
    private String data;
    private String polynomial;

    public CrcRequest() {}

    public CrcRequest(String data, String polynomial) {
        this.data = data;
        this.polynomial = polynomial;
    }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getPolynomial() { return polynomial; }
    public void setPolynomial(String polynomial) { this.polynomial = polynomial; }
}
