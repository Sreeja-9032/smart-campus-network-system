package com.smartcampus.network;

import com.smartcampus.network.model.*;
import com.smartcampus.network.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmartCampusApplicationTests {

    private TopologyService topologyService;
    private DijkstraRoutingService dijkstraService;
    private FramingService framingService;
    private CrcService crcService;
    private SlidingWindowService slidingWindowService;
    private GoBackNService goBackNService;
    private Ns2MetricsService ns2MetricsService;
    private PacketAnalysisService packetService;

    @BeforeEach
    void setUp() {
        topologyService = new TopologyService();
        dijkstraService = new DijkstraRoutingService(topologyService);
        framingService = new FramingService();
        crcService = new CrcService();
        slidingWindowService = new SlidingWindowService();
        goBackNService = new GoBackNService();
        ns2MetricsService = new Ns2MetricsService();
        packetService = new PacketAnalysisService();
    }

    // ==========================================
    // 1. FRAMING TESTS
    // ==========================================
    @Test
    void testFramingNormalMessage() {
        FramingRequest req = new FramingRequest("CAMPUS NETWORK", "FLAG", "ESC", 10);
        FramingResponse resp = framingService.encodeMessage(req);

        assertTrue(resp.isSuccess());
        assertEquals(2, resp.getTotalFrames());
        assertEquals(0, resp.getFrames().get(0).getEscapedFlagCount());
    }

    @Test
    void testFramingMessageContainingFlag() {
        FramingRequest req = new FramingRequest("DATA FLAG PACKET", "FLAG", "ESC", 20);
        FramingResponse resp = framingService.encodeMessage(req);

        assertTrue(resp.isSuccess());
        assertTrue(resp.getFrames().get(0).getStuffedPayload().contains("ESCFLAG"));
        assertEquals(1, resp.getFrames().get(0).getEscapedFlagCount());
    }

    @Test
    void testFramingMessageContainingEsc() {
        FramingRequest req = new FramingRequest("CONTROL ESC CHAR", "FLAG", "ESC", 20);
        FramingResponse resp = framingService.encodeMessage(req);

        assertTrue(resp.isSuccess());
        assertTrue(resp.getFrames().get(0).getStuffedPayload().contains("ESCESC"));
        assertEquals(1, resp.getFrames().get(0).getEscapedEscCount());
    }

    @Test
    void testFramingReceiverReconstructsOriginalMessage() {
        String original = "LAB FLAG DATA ESC TEST";
        FramingRequest encodeReq = new FramingRequest(original, "FLAG", "ESC", 10);
        FramingResponse encodeResp = framingService.encodeMessage(encodeReq);

        List<String> framedDataList = encodeResp.getFrames().stream()
                .map(FrameItem::getFramedData)
                .toList();

        DeframingRequest decodeReq = new DeframingRequest(framedDataList, "FLAG", "ESC");
        DeframingResponse decodeResp = framingService.decodeFrames(decodeReq);

        assertTrue(decodeResp.isSuccess());
        assertEquals(original, decodeResp.getReconstructedMessage());
    }

    // ==========================================
    // 2. CRC ERROR DETECTION TESTS
    // ==========================================
    @Test
    void testCrcValidBinaryInput() {
        CrcRequest req = new CrcRequest("100100", "1011");
        CrcResponse resp = crcService.calculateCrc(req);

        assertTrue(resp.isValid());
        assertEquals("101", resp.getRemainder());
        assertEquals("100100101", resp.getTransmittedCodeword());
    }

    @Test
    void testCrcInvalidBinaryInput() {
        CrcRequest req = new CrcRequest("10112A", "1011");
        CrcResponse resp = crcService.calculateCrc(req);

        assertFalse(resp.isValid());
        assertTrue(resp.getMessage().contains("Invalid binary data"));
    }

    @Test
    void testCrcVerificationSuccess() {
        // Compute valid codeword
        CrcRequest req = new CrcRequest("1101011011", "10011");
        CrcResponse resp = crcService.calculateCrc(req);

        // Verify valid codeword
        CrcVerifyRequest verifyReq = new CrcVerifyRequest(resp.getTransmittedCodeword(), "10011");
        CrcVerifyResponse verifyResp = crcService.verifyCrc(verifyReq);

        assertTrue(verifyResp.isValidFormat());
        assertTrue(verifyResp.isDataIntegrityPass());
        assertFalse(verifyResp.isErrorDetected());
        assertEquals("0000", verifyResp.getRemainder());
    }

    @Test
    void testCrcCorruptedDataDetection() {
        // Corrupt transmitted codeword by inverting a bit
        CrcVerifyRequest verifyReq = new CrcVerifyRequest("101100010", "1011"); // last bit inverted from 1 to 0
        CrcVerifyResponse verifyResp = crcService.verifyCrc(verifyReq);

        assertTrue(verifyResp.isValidFormat());
        assertFalse(verifyResp.isDataIntegrityPass());
        assertTrue(verifyResp.isErrorDetected());
        assertNotEquals("000", verifyResp.getRemainder());
    }

    // ==========================================
    // 3. SLIDING WINDOW TESTS
    // ==========================================
    @Test
    void testSlidingWindowSizeOne() {
        SlidingWindowRequest req = new SlidingWindowRequest(4, 1);
        SlidingWindowResponse resp = slidingWindowService.simulate(req);

        assertTrue(resp.isSuccess());
        assertTrue(resp.getTotalSteps() > 0);
        assertEquals(4, resp.getTotalFrames());
    }

    @Test
    void testSlidingWindowGreaterThanOne() {
        SlidingWindowRequest req = new SlidingWindowRequest(8, 4);
        SlidingWindowResponse resp = slidingWindowService.simulate(req);

        assertTrue(resp.isSuccess());
        assertEquals(8, resp.getTotalFrames());
        assertEquals(4, resp.getWindowSize());
    }

    @Test
    void testSlidingWindowTotalFramesSmallerThanWindow() {
        SlidingWindowRequest req = new SlidingWindowRequest(3, 5);
        SlidingWindowResponse resp = slidingWindowService.simulate(req);

        assertTrue(resp.isSuccess());
        assertEquals(3, resp.getTotalFrames());
    }

    @Test
    void testSlidingWindowMultipleAdvancements() {
        SlidingWindowRequest req = new SlidingWindowRequest(6, 3);
        SlidingWindowResponse resp = slidingWindowService.simulate(req);

        assertTrue(resp.isSuccess());
        // Verify final step acknowledges all frames
        SlidingWindowStep lastStep = resp.getSteps().get(resp.getSteps().size() - 1);
        assertEquals(6, lastStep.getAcknowledgedFrame());
    }

    // ==========================================
    // 4. GO-BACK-N ARQ TESTS
    // ==========================================
    @Test
    void testGoBackNSuccessfulTransmissionWithoutLoss() {
        GoBackNRequest req = new GoBackNRequest(6, 3, -1);
        GoBackNResponse resp = goBackNService.simulate(req);

        assertTrue(resp.isSuccess());
        assertEquals(0, resp.getRetransmissionCount());
        assertEquals(6, resp.getTotalTransmissions());
    }

    @Test
    void testGoBackNWithFrameLossAndRetransmission() {
        GoBackNRequest req = new GoBackNRequest(8, 4, 3);
        GoBackNResponse resp = goBackNService.simulate(req);

        assertTrue(resp.isSuccess());
        assertTrue(resp.getRetransmissionCount() > 0);
        // Step trace contains LOSS and DISCARD events
        boolean hasLoss = resp.getSteps().stream().anyMatch(s -> "LOSS".equals(s.getEventType()));
        boolean hasDiscard = resp.getSteps().stream().anyMatch(s -> "DISCARD".equals(s.getEventType()));
        boolean hasRetransmit = resp.getSteps().stream().anyMatch(s -> "RETRANSMIT".equals(s.getEventType()));

        assertTrue(hasLoss);
        assertTrue(hasDiscard);
        assertTrue(hasRetransmit);
    }

    // ==========================================
    // 5. DIJKSTRA ROUTING TESTS
    // ==========================================
    @Test
    void testDijkstraShortestRoute() {
        DijkstraResponse resp = dijkstraService.calculateShortestPath("CS_DEPT", "SERVER_ROOM");

        assertTrue(resp.isSuccess());
        assertTrue(resp.getTotalCost() > 0);
        assertFalse(resp.getPathNodeIds().isEmpty());
        assertEquals("CS_DEPT", resp.getPathNodeIds().get(0));
        assertEquals("SERVER_ROOM", resp.getPathNodeIds().get(resp.getPathNodeIds().size() - 1));
    }

    @Test
    void testDijkstraDifferentSourceAndDestination() {
        DijkstraResponse resp = dijkstraService.calculateShortestPath("ECE_DEPT", "LIBRARY_DEPT");

        assertTrue(resp.isSuccess());
        assertFalse(resp.getIntermediateDevices().isEmpty());
    }

    @Test
    void testDijkstraSameSourceAndDestination() {
        DijkstraResponse resp = dijkstraService.calculateShortestPath("CS_DEPT", "CS_DEPT");

        assertTrue(resp.isSuccess());
        assertEquals(0, resp.getTotalCost());
    }

    @Test
    void testDijkstraInvalidNode() {
        DijkstraResponse resp = dijkstraService.calculateShortestPath("INVALID_NODE", "SERVER_ROOM");

        assertFalse(resp.isSuccess());
        assertTrue(resp.getMessage().contains("Invalid source node"));
    }

    // ==========================================
    // 6. METRICS & PERFORMANCE TESTS
    // ==========================================
    @Test
    void testMetricsSampleSimulation() {
        Ns2MetricsResponse resp = ns2MetricsService.runSampleSimulation();

        assertTrue(resp.isSuccess());
        assertTrue(resp.getSentPackets() > 0);
        assertTrue(resp.getReceivedPackets() > 0);
        assertTrue(resp.getPacketDeliveryRatioPercentage() > 90.0);
        assertTrue(resp.getThroughputMbps() > 0.0);
        assertTrue(resp.getAverageDelayMs() > 0.0);
        assertNotNull(resp.getFormulaExplanations());
    }

    @Test
    void testWiresharkCsvParsing() {
        String csv = "\"No.\",\"Time\",\"Source\",\"Destination\",\"Protocol\",\"Length\",\"Info\"\n" +
                     "\"1\",\"0.000000\",\"192.168.1.10\",\"192.168.5.10\",\"TCP\",\"66\",\"SYN\"\n" +
                     "\"2\",\"0.002000\",\"192.168.5.10\",\"192.168.1.10\",\"TCP\",\"66\",\"SYN-ACK\"";
        PacketAnalysisResponse resp = packetService.parseWiresharkCsv(csv);

        assertTrue(resp.isSuccess());
        assertEquals(2, resp.getTotalPackets());
        assertEquals("Imported Wireshark CSV Capture", resp.getDataSource());
        assertEquals(2, resp.getProtocolBreakdown().get("TCP"));
    }
}
