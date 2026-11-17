package com.smartcampus.network.service;

import com.smartcampus.network.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CrcService {

    public CrcResponse calculateCrc(CrcRequest request) {
        if (request == null || request.getData() == null || request.getData().trim().isEmpty()) {
            return CrcResponse.error("Binary data must not be empty.");
        }
        if (request.getPolynomial() == null || request.getPolynomial().trim().isEmpty()) {
            return CrcResponse.error("Generator polynomial must not be empty.");
        }

        String data = request.getData().replaceAll("\\s+", "");
        String poly = request.getPolynomial().replaceAll("\\s+", "");

        if (!data.matches("^[01]+$")) {
            return CrcResponse.error("Invalid binary data: '" + data + "'. Only '0' and '1' are allowed.");
        }
        if (!poly.matches("^[01]+$")) {
            return CrcResponse.error("Invalid generator polynomial: '" + poly + "'. Only '0' and '1' are allowed.");
        }
        if (poly.length() < 2) {
            return CrcResponse.error("Generator polynomial must have degree >= 1 (length >= 2 bits).");
        }
        if (poly.charAt(0) != '1') {
            return CrcResponse.error("Generator polynomial must start with '1' (highest degree term must be present).");
        }

        int polyLen = poly.length();
        int zerosToAppend = polyLen - 1;
        String appendedZeros = "0".repeat(zerosToAppend);
        String dividend = data + appendedZeros;

        List<String> divisionSteps = new ArrayList<>();
        String remainder = performModulo2Division(dividend, poly, divisionSteps);

        String transmittedCodeword = data + remainder;

        CrcResponse response = new CrcResponse();
        response.setValid(true);
        response.setMessage("CRC calculation successful. " + zerosToAppend + " bits appended for polynomial degree " + zerosToAppend + ".");
        response.setOriginalData(data);
        response.setPolynomial(poly);
        response.setAppendedZeros(appendedZeros);
        response.setDividend(dividend);
        response.setRemainder(remainder);
        response.setTransmittedCodeword(transmittedCodeword);
        response.setDivisionSteps(divisionSteps);

        return response;
    }

    public CrcVerifyResponse verifyCrc(CrcVerifyRequest request) {
        if (request == null || request.getReceivedCodeword() == null || request.getReceivedCodeword().trim().isEmpty()) {
            return CrcVerifyResponse.formatError("Received codeword must not be empty.");
        }
        if (request.getPolynomial() == null || request.getPolynomial().trim().isEmpty()) {
            return CrcVerifyResponse.formatError("Generator polynomial must not be empty.");
        }

        String codeword = request.getReceivedCodeword().replaceAll("\\s+", "");
        String poly = request.getPolynomial().replaceAll("\\s+", "");

        if (!codeword.matches("^[01]+$")) {
            return CrcVerifyResponse.formatError("Invalid codeword: '" + codeword + "'. Only '0' and '1' are allowed.");
        }
        if (!poly.matches("^[01]+$")) {
            return CrcVerifyResponse.formatError("Invalid generator polynomial: '" + poly + "'. Only '0' and '1' are allowed.");
        }
        if (codeword.length() < poly.length()) {
            return CrcVerifyResponse.formatError("Codeword length (" + codeword.length() + ") must be at least polynomial length (" + poly.length() + ").");
        }

        List<String> divisionSteps = new ArrayList<>();
        String remainder = performModulo2Division(codeword, poly, divisionSteps);

        boolean hasError = remainder.contains("1");

        CrcVerifyResponse response = new CrcVerifyResponse();
        response.setValidFormat(true);
        response.setDataIntegrityPass(!hasError);
        response.setErrorDetected(hasError);
        response.setReceivedCodeword(codeword);
        response.setPolynomial(poly);
        response.setRemainder(remainder);
        response.setDivisionSteps(divisionSteps);

        if (!hasError) {
            response.setMessage("CRC Verification PASSED: Remainder is zero (" + remainder + "). No transmission errors detected. Frame is accepted.");
        } else {
            response.setMessage("CRC Verification FAILED: Non-zero syndrome remainder (" + remainder + ") detected. Bit corruption occurred during transmission! Frame must be discarded/retransmitted.");
        }

        return response;
    }

    private String performModulo2Division(String dividend, String polynomial, List<String> steps) {
        char[] bits = dividend.toCharArray();
        int polyLen = polynomial.length();
        int limit = bits.length - polyLen;

        steps.add("Initial dividend: " + new String(bits));

        for (int i = 0; i <= limit; i++) {
            if (bits[i] == '1') {
                steps.add("Step " + (steps.size()) + ": Bit at index " + i + " is 1 -> XOR with polynomial " + polynomial);
                for (int j = 0; j < polyLen; j++) {
                    bits[i + j] = (char) ('0' + ((bits[i + j] - '0') ^ (polynomial.charAt(j) - '0')));
                }
            }
        }

        // Remainder is the last (polyLen - 1) bits
        int remainderStart = bits.length - (polyLen - 1);
        String remainder = new String(bits, remainderStart, polyLen - 1);
        steps.add("Final Remainder (Syndrome): " + remainder);
        return remainder;
    }
}
