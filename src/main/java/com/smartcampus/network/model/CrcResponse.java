package com.smartcampus.network.model;

import java.util.List;

public class CrcResponse {
    private boolean valid;
    private String message;
    private String originalData;
    private String polynomial;
    private String appendedZeros;
    private String dividend;
    private String remainder;
    private String transmittedCodeword;
    private List<String> divisionSteps;

    public CrcResponse() {}

    public static CrcResponse error(String message) {
        CrcResponse resp = new CrcResponse();
        resp.setValid(false);
        resp.setMessage(message);
        return resp;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getOriginalData() { return originalData; }
    public void setOriginalData(String originalData) { this.originalData = originalData; }

    public String getPolynomial() { return polynomial; }
    public void setPolynomial(String polynomial) { this.polynomial = polynomial; }

    public String getAppendedZeros() { return appendedZeros; }
    public void setAppendedZeros(String appendedZeros) { this.appendedZeros = appendedZeros; }

    public String getDividend() { return dividend; }
    public void setDividend(String dividend) { this.dividend = dividend; }

    public String getRemainder() { return remainder; }
    public void setRemainder(String remainder) { this.remainder = remainder; }

    public String getTransmittedCodeword() { return transmittedCodeword; }
    public void setTransmittedCodeword(String transmittedCodeword) { this.transmittedCodeword = transmittedCodeword; }

    public List<String> getDivisionSteps() { return divisionSteps; }
    public void setDivisionSteps(List<String> divisionSteps) { this.divisionSteps = divisionSteps; }
}
