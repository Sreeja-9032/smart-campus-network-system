package com.smartcampus.network.service;

import com.smartcampus.network.model.PacketAnalysisResponse;
import com.smartcampus.network.model.PacketRecord;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;

@Service
public class PacketAnalysisService {

    public PacketAnalysisResponse generateSampleCampusPackets() {
        List<PacketRecord> packets = new ArrayList<>();

        // Campus Scenario: CS Lab accessing Campus Web Portal & ECE IoT Sensors transmitting telemetry
        packets.add(new PacketRecord(1, 0.000000, "192.168.1.10", "192.168.5.10", "ICMP", 64, "Success", "Echo (ping) request  id=0x0001, seq=1/256, ttl=64", "SIMULATED"));
        packets.add(new PacketRecord(2, 0.003120, "192.168.5.10", "192.168.1.10", "ICMP", 64, "Success", "Echo (ping) reply    id=0x0001, seq=1/256, ttl=64 (reply in 3.12ms)", "SIMULATED"));

        packets.add(new PacketRecord(3, 0.015400, "192.168.1.10", "192.168.0.1", "DNS", 78, "Success", "Standard query 0x3f12 A portal.campus.edu", "SIMULATED"));
        packets.add(new PacketRecord(4, 0.018240, "192.168.0.1", "192.168.1.10", "DNS", 94, "Success", "Standard query response 0x3f12 A portal.campus.edu -> 192.168.5.10", "SIMULATED"));

        // TCP 3-Way Handshake
        packets.add(new PacketRecord(5, 0.024100, "192.168.1.10", "192.168.5.10", "TCP", 66, "Handshake", "51240 → 8080 [SYN] Seq=0 Win=64240 Len=0 MSS=1460 WS=256", "SIMULATED"));
        packets.add(new PacketRecord(6, 0.026850, "192.168.5.10", "192.168.1.10", "TCP", 66, "Handshake", "8080 → 51240 [SYN, ACK] Seq=0 Ack=1 Win=65160 Len=0 MSS=1460", "SIMULATED"));
        packets.add(new PacketRecord(7, 0.027100, "192.168.1.10", "192.168.5.10", "TCP", 54, "Handshake", "51240 → 8080 [ACK] Seq=1 Ack=1 Win=64240 Len=0", "SIMULATED"));

        // Application Data (HTTP)
        packets.add(new PacketRecord(8, 0.031500, "192.168.1.10", "192.168.5.10", "HTTP", 452, "Success", "GET /api/student-portal HTTP/1.1 (Host: portal.campus.edu)", "SIMULATED"));
        packets.add(new PacketRecord(9, 0.038900, "192.168.5.10", "192.168.1.10", "TCP", 54, "Success", "8080 → 51240 [ACK] Seq=1 Ack=399 Win=65160 Len=0", "SIMULATED"));
        packets.add(new PacketRecord(10, 0.042100, "192.168.5.10", "192.168.1.10", "HTTP", 1430, "Success", "HTTP/1.1 200 OK  (text/html, 1376 bytes)", "SIMULATED"));

        // UDP IoT telemetry from ECE lab
        packets.add(new PacketRecord(11, 0.055300, "192.168.2.10", "192.168.5.10", "UDP", 128, "Success", "Source port: 9000  Destination port: 9000  Length: 120 (IoT Telemetry)", "SIMULATED"));
        packets.add(new PacketRecord(12, 0.062100, "192.168.3.10", "192.168.5.10", "TCP", 66, "Handshake", "Library catalogue sync 49810 → 8080 [SYN] Seq=0", "SIMULATED"));
        packets.add(new PacketRecord(13, 0.065200, "192.168.4.10", "192.168.5.10", "HTTP", 520, "Success", "POST /api/exam-results HTTP/1.1 from Admin Office", "SIMULATED"));
        packets.add(new PacketRecord(14, 0.071000, "192.168.0.1", "224.0.0.1", "IGMP", 46, "Broadcast", "Membership Report group 224.0.0.1", "SIMULATED"));

        Map<String, Integer> breakdown = computeProtocolBreakdown(packets);

        PacketAnalysisResponse response = new PacketAnalysisResponse();
        response.setSuccess(true);
        response.setMessage("Generated " + packets.size() + " simulated campus packets across TCP, UDP, ICMP, DNS, and HTTP protocols.");
        response.setDataSource("Simulated Campus Traffic");
        response.setTotalPackets(packets.size());
        response.setProtocolBreakdown(breakdown);
        response.setPackets(packets);

        return response;
    }

    public PacketAnalysisResponse parseWiresharkCsv(String csvContent) {
        if (csvContent == null || csvContent.trim().isEmpty()) {
            return PacketAnalysisResponse.error("CSV content is empty. Please provide Wireshark CSV export data.");
        }

        List<PacketRecord> packets = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
            String line;
            boolean isHeader = true;
            int lineNum = 0;

            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                // Simple check for Wireshark CSV header
                if (isHeader) {
                    isHeader = false;
                    if (line.toLowerCase().contains("no.") || line.toLowerCase().contains("source") || line.toLowerCase().contains("protocol")) {
                        continue; // Skip header line
                    }
                }

                List<String> tokens = parseCsvLine(line);
                if (tokens.size() >= 5) {
                    try {
                        int num = packets.size() + 1;
                        double time = 0.0;
                        String src = "";
                        String dst = "";
                        String proto = "UNKNOWN";
                        int len = 0;
                        String info = "";

                        // Standard Wireshark CSV columns: No., Time, Source, Destination, Protocol, Length, Info
                        if (tokens.size() >= 7) {
                            try { num = Integer.parseInt(clean(tokens.get(0))); } catch (Exception ignored) {}
                            try { time = Double.parseDouble(clean(tokens.get(1))); } catch (Exception ignored) {}
                            src = clean(tokens.get(2));
                            dst = clean(tokens.get(3));
                            proto = clean(tokens.get(4)).toUpperCase();
                            try { len = Integer.parseInt(clean(tokens.get(5)).replaceAll("[^0-9]", "")); } catch (Exception ignored) {}
                            info = clean(tokens.get(6));
                        } else if (tokens.size() == 6) {
                            try { num = Integer.parseInt(clean(tokens.get(0))); } catch (Exception ignored) {}
                            src = clean(tokens.get(1));
                            dst = clean(tokens.get(2));
                            proto = clean(tokens.get(3)).toUpperCase();
                            try { len = Integer.parseInt(clean(tokens.get(4)).replaceAll("[^0-9]", "")); } catch (Exception ignored) {}
                            info = clean(tokens.get(5));
                        } else {
                            src = clean(tokens.get(0));
                            dst = clean(tokens.get(1));
                            proto = clean(tokens.get(2)).toUpperCase();
                            try { len = Integer.parseInt(clean(tokens.get(3)).replaceAll("[^0-9]", "")); } catch (Exception ignored) {}
                            info = clean(tokens.get(4));
                        }

                        packets.add(new PacketRecord(num, time, src, dst, proto, len, "Imported", info, "IMPORTED_WIRESHARK"));
                    } catch (Exception e) {
                        // skip corrupted row
                    }
                }
            }
        } catch (Exception e) {
            return PacketAnalysisResponse.error("Failed to parse Wireshark CSV: " + e.getMessage());
        }

        if (packets.isEmpty()) {
            return PacketAnalysisResponse.error("No valid packet rows could be extracted from the CSV. Ensure the format contains 'No.', 'Source', 'Destination', 'Protocol', 'Length', and 'Info'.");
        }

        Map<String, Integer> breakdown = computeProtocolBreakdown(packets);

        PacketAnalysisResponse response = new PacketAnalysisResponse();
        response.setSuccess(true);
        response.setMessage("Successfully imported and analyzed " + packets.size() + " packets from Wireshark CSV export.");
        response.setDataSource("Imported Wireshark CSV Capture");
        response.setTotalPackets(packets.size());
        response.setProtocolBreakdown(breakdown);
        response.setPackets(packets);

        return response;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        result.add(cur.toString());
        return result;
    }

    private String clean(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("^\"|\"$", "");
    }

    private Map<String, Integer> computeProtocolBreakdown(List<PacketRecord> packets) {
        Map<String, Integer> breakdown = new HashMap<>();
        for (PacketRecord p : packets) {
            String proto = p.getProtocol() != null ? p.getProtocol() : "OTHER";
            breakdown.put(proto, breakdown.getOrDefault(proto, 0) + 1);
        }
        return breakdown;
    }
}
