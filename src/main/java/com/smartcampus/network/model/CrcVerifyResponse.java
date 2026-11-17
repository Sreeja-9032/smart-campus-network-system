package com.smartcampus.network.model;

import java.util.List;

public class CrcVerifyResponse {
    private boolean validFormat;
    private boolean dataIntegrityPass;
    private String message;
    private String receivedCodeword;
    private String polynomial;
    private String remainder;
    private boolean errorDetected;
    private List<String> divisionSteps;

    public CrcVerifyResponse() {}

    public static CrcVerifyResponse formatError(String message) {
        CrcVerifyResponse resp = new CrcVerifyResponse();
        resp.setValidFormat(false);
        resp.setDataIntegrityPass(false);
        resp.setErrorDetected(true);
        resp.setMessage(message);
        return resp;
    }

    public boolean isValidFormat() { return validFormat; }
    public void setValidFormat(boolean validFormat) { this.validFormat = validFormat; }

    public boolean isDataIntegrityPass() { return dataIntegrityPass; }
    public void setDataIntegrityPass(boolean dataIntegrityPass) { this.dataIntegrityPass = dataIntegrityPass; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getReceivedCodeword() { return receivedCodeword; }
    public void setReceivedCodeword(String receivedCodeword) { this.receivedCodeword = receivedCodeword; }

    public String getPolynomial() { return polynomial; }
    public void setPolynomial(String polynomial) { this.polynomial = polynomial; }

    public String getRemainder() { return remainder; }
    public void setRemainder(String remainder) { this.remainder = remainder; }

    public boolean isErrorDetected() { return errorDetected; }
    public void setErrorDetected(boolean errorDetected) { this.errorDetected = errorDetected; }

    public List<String> getDivisionSteps() { return divisionSteps; }
    public void setDivisionSteps(List<String> divisionSteps) { this.divisionSteps = divisionSteps; }
}
