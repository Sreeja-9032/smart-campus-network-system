# SMART CAMPUS NETWORK MONITORING AND ROUTING SYSTEM (SCNM)
**B.Tech Computer Networks and Internet Protocols (CNIP) Laboratory Project**

---

## 1. Project Overview

The **Smart Campus Network Monitoring and Routing System (SCNM)** is a full-stack interactive network simulation application designed for university laboratory demonstrations. It models a campus-wide Local Area Network (LAN) interconnecting five key departments through an enterprise core network architecture:

1. **Computer Science Department** (`192.168.1.10`)
2. **Electronics & Communication Engineering (ECE)** (`192.168.2.10`)
3. **Digital Library** (`192.168.3.10`)
4. **Administrative Office** (`192.168.4.10`)
5. **Server Room / Data Center** (`192.168.5.10`)
*Plus Backbone Core Switch (`192.168.0.2`), Core Router (`192.168.0.1`), and ISP Gateway (`10.0.0.1`)*.

The system demonstrates core data communication and networking concepts across the OSI model:
- **Logical Campus Topology & Device Monitoring**
- **Data Link Framing** (Byte Stuffing with delimiters and escaping)
- **Error Detection** (Cyclic Redundancy Check - CRC with modulo-2 division & syndrome verification)
- **Flow Control** (Sliding Window Protocol with dynamic sender window visualization)
- **Error Recovery** (Go-Back-N ARQ with loss simulation, out-of-order frame discard, and window retransmission)
- **Network Layer Routing** (Dijkstra's shortest-path least-cost algorithm)
- **Packet Inspection** (Wireshark capture analysis & CSV import)
- **Network Performance Evaluation** (NS2 trace evaluation: Throughput, Packet Loss, PDR, and Delay)

---

## 2. Technology Stack

- **Frontend:**
  - HTML5, CSS3 (Custom Dark Theme UI, Responsive Layout)
  - Vanilla JavaScript (ES6+, Fetch API, Canvas 2D Graphics)
- **Backend:**
  - Java 17+ (Compiled and tested on Oracle JDK 26)
  - Spring Boot 3.2.5 (Spring Web REST Controllers)
  - Maven 3.8+ (with self-contained `mvnw.cmd` wrapper)
  - JUnit 5 (Unit and Integration Test Suite)
- **Embedded Static Serving:**
  - Embedded Apache Tomcat (Spring Boot serves `index.html`, `style.css`, and `script.js` directly on port `8081`).

---

## 3. Project Architecture & Directory Structure

```text
sreejacampusnetwork/
├── pom.xml                                   # Maven project configuration
├── mvnw.cmd                                  # Self-contained Maven Windows wrapper
├── mvnw                                      # Maven Unix/Linux wrapper
├── index.html                                # Main frontend dashboard & lab tabs
├── style.css                                 # Campus network dark theme CSS
├── script.js                                 # Frontend controller & REST client
├── campus_simulation.tcl                     # Authentic NS2 TCL simulation script
├── sample_wireshark.csv                      # Sample Wireshark CSV packet dissection export
├── README.md                                 # Complete documentation & lab guide
├── src/
│   ├── main/
│   │   ├── java/com/smartcampus/network/
│   │   │   ├── SmartCampusApplication.java   # Spring Boot entry point
│   │   │   ├── config/
│   │   │   │   └── WebConfig.java            # CORS & static resource mapping
│   │   │   ├── model/
│   │   │   │   ├── TopologyNode.java         # Campus node entity
│   │   │   │   ├── TopologyEdge.java         # Positive-cost graph edge
│   │   │   │   ├── NetworkTopology.java      # Graph topology wrapper
│   │   │   │   ├── Device.java               # Campus device model
│   │   │   │   ├── DijkstraRequest.java      # Source/Destination routing request
│   │   │   │   ├── DijkstraResponse.java     # Shortest path & hops response
│   │   │   │   ├── RouteHop.java             # Individual edge hop cost
│   │   │   │   ├── FramingRequest.java       # Byte stuffing parameters
│   │   │   │   ├── FramingResponse.java      # Stuffed frames & transformations
│   │   │   │   ├── FrameItem.java            # Frame data model
│   │   │   │   ├── DeframingRequest.java     # Receiver de-stuffing request
│   │   │   │   ├── DeframingResponse.java    # Reconstructed message
│   │   │   │   ├── CrcRequest.java           # Binary data & polynomial
│   │   │   │   ├── CrcResponse.java          # Remainder & codeword
│   │   │   │   ├── CrcVerifyRequest.java     # Codeword verification request
│   │   │   │   ├── CrcVerifyResponse.java    # Syndrome check result
│   │   │   │   ├── SlidingWindowRequest.java # Window size & total frames
│   │   │   │   ├── SlidingWindowResponse.java# Step-by-step timeline
│   │   │   │   ├── SlidingWindowStep.java    # Single step state
│   │   │   │   ├── GoBackNRequest.java       # GBN parameters & lost frame
│   │   │   │   ├── GoBackNResponse.java      # GBN recovery steps
│   │   │   │   ├── GoBackNStep.java          # GBN step events
│   │   │   │   ├── PacketRecord.java         # Packet capture record
│   │   │   │   ├── PacketImportRequest.java  # Wireshark CSV payload
│   │   │   │   ├── PacketAnalysisResponse.java# Packet breakdown
│   │   │   │   ├── Ns2MetricsResponse.java   # Throughput, loss, PDR, delay
│   │   │   │   └── Ns2TraceImportRequest.java# NS2 trace string
│   │   │   ├── service/
│   │   │   │   ├── TopologyService.java      # Campus graph initialization & devices
│   │   │   │   ├── DijkstraRoutingService.java# Pure Java Dijkstra implementation
│   │   │   │   ├── FramingService.java       # Byte stuffing & de-stuffing logic
│   │   │   │   ├── CrcService.java           # Modulo-2 binary division & XOR verification
│   │   │   │   ├── SlidingWindowService.java # Flow control timeline generator
│   │   │   │   ├── GoBackNService.java       # ARQ error recovery state machine
│   │   │   │   ├── PacketAnalysisService.java# Traffic generator & Wireshark CSV parser
│   │   │   │   └── Ns2MetricsService.java    # NS2 trace parser & formula calculator
│   │   │   └── controller/
│   │   │       ├── NetworkTopologyController.java# /api/network/topology, /api/routing/dijkstra
│   │   │       ├── DeviceController.java     # /api/devices, /api/devices/{id}/toggle
│   │   │       ├── FramingController.java    # /api/framing/encode, /api/framing/decode
│   │   │       ├── CrcController.java        # /api/crc/calculate, /api/crc/verify
│   │   │       ├── SlidingWindowController.java# /api/sliding-window/simulate
│   │   │       ├── GoBackNController.java    # /api/gobackn/simulate
│   │   │       ├── PacketController.java     # /api/packets/generate, /api/packets/import
│   │   │       └── Ns2MetricsController.java # /api/metrics/analyze, /api/ns2/tcl-script
│   │   └── resources/
│   │       ├── application.properties        # Server port (8081) configuration
│   │       ├── campus_simulation.tcl         # NS2 script resource
│   │       ├── sample_wireshark.csv          # Sample CSV resource
│   │       └── static/                       # Static web assets served by Spring Boot
│   └── test/
│       └── java/com/smartcampus/network/
│           └── SmartCampusApplicationTests.java# 20 JUnit 5 unit & integration tests
```

---

## 4. How to Run the Application

### Prerequisites
- Java Development Kit (JDK 17 or higher, e.g., Java 17, 21, or 26).
- An internet browser (Chrome, Edge, Firefox).

### Option A: Run Using Pre-built Executable JAR (Fastest)
From the project root directory:
```powershell
java -jar target\smartcampus-network-1.0.0.jar
```

### Option B: Build and Run from Source Using Maven Wrapper
```powershell
# Run unit tests
.\mvnw.cmd test

# Build executable package
.\mvnw.cmd package

# Start the Spring Boot application
java -jar target\smartcampus-network-1.0.0.jar
```

### Accessing the Web Dashboard
Once the server displays `Tomcat started on port 8081 (http)`:
1. Open your browser and navigate to:
   ```text
   http://localhost:8081
   ```
2. The frontend loads automatically from Spring Boot with live REST API integration!
3. Alternatively, you can double-click `index.html` directly in file explorer; it automatically connects to `http://localhost:8081/api/...` via Cross-Origin Resource Sharing (CORS).

---

## 5. REST API Endpoints Specification

| Method | Endpoint | Request Body | Description |
|---|---|---|---|
| `GET` | `/api/network/topology` | None | Returns campus nodes, links, and metric costs. |
| `GET` | `/api/devices` | None | Lists all campus devices, types, statuses, and latencies. |
| `POST` | `/api/devices/{id}/toggle` | None | Toggles device status (ONLINE/OFFLINE) to simulate link failure. |
| `POST` | `/api/routing/dijkstra` | `{"source": "CS_DEPT", "destination": "SERVER_ROOM"}` | Calculates shortest path, hops, and total metric cost. |
| `POST` | `/api/framing/encode` | `{"message": "TEXT", "flagDelimiter": "FLAG", "escDelimiter": "ESC", "frameSize": 10}` | Divides message into byte-stuffed frames. |
| `POST` | `/api/framing/decode` | `{"framedDataList": ["..."], "flagDelimiter": "FLAG", "escDelimiter": "ESC"}` | De-stuffs frames and reconstructs original message. |
| `POST` | `/api/crc/calculate` | `{"data": "100100", "polynomial": "1011"}` | Performs modulo-2 binary division; returns remainder & codeword. |
| `POST` | `/api/crc/verify` | `{"receivedCodeword": "100100101", "polynomial": "1011"}` | Verifies codeword integrity at receiver (syndrome check). |
| `POST` | `/api/sliding-window/simulate` | `{"windowSize": 4, "totalFrames": 8}` | Generates step-by-step sliding window timeline. |
| `POST` | `/api/gobackn/simulate` | `{"windowSize": 4, "totalFrames": 8, "lostFrame": 3}` | Simulates Go-Back-N loss, timeout, and window recovery. |
| `GET` | `/api/packets/generate` | None | Generates simulated campus LAN traffic (TCP, UDP, ICMP, DNS, HTTP). |
| `POST` | `/api/packets/import` | `{"csvContent": "..."}` | Parses and inspects imported Wireshark CSV packet dissection. |
| `POST` | `/api/metrics/analyze` | `{"traceContent": "..."}` *(Optional)* | Analyzes NS2 trace file or runs demonstration mode. |
| `GET` | `/api/metrics/sample-trace` | None | Returns sample 200-packet NS2 trace data. |
| `GET` | `/api/ns2/tcl-script` | None | Returns the `campus_simulation.tcl` script. |

---

## 6. Networking Concepts & Algorithm Details

### A. Dijkstra's Shortest-Path Algorithm
- **Graph Representation:** Undirected graph $G = (V, E)$ where vertices $V$ represent campus hosts and switches, and edges $E$ represent network links with positive metric weights (delay/bandwidth cost).
- **Execution:** Uses a `PriorityQueue` indexed by cumulative distance. Starting from the source node with distance 0, relaxes neighboring edges iteratively:
  $$d(v) = \min(d(v), d(u) + w(u, v))$$
- **Output:** Path node sequence, list of intermediate switches, hop-by-hop cost breakdown, and total route metric.

### B. Byte Stuffing (Character-Oriented Framing)
- **Delimiters:** Special `FLAG` sequence marks the beginning and end of each frame: `FLAG [ payload ] FLAG`.
- **Escaping:** If the `FLAG` pattern appears inside user data, the sender prepends an `ESC` character (`ESC FLAG`). If the `ESC` character itself appears, it is escaped as `ESC ESC`.
- **Receiver De-stuffing:** Strips boundary flags and reverses the escape sequences to guarantee 100% transparent and lossless data reconstruction.

### C. CRC (Cyclic Redundancy Check) Error Detection
- **Generator Polynomial:** Divisor $P(x)$ of degree $k-1$ (e.g., $1011$ has degree 3).
- **Sender:** Appends $k-1$ zeros to data $D$. Performs modulo-2 binary division using bitwise XOR operations. The remainder $R$ forms the CRC bits. Transmitted codeword = $D \cdot 2^{k-1} \oplus R$.
- **Receiver:** Divides the received codeword by the same polynomial $P(x)$.
  - If remainder (syndrome) is **all zeros (`000`)**, no bit errors were detected $\rightarrow$ Frame accepted.
  - If remainder is **non-zero**, transmission noise corrupted one or more bits $\rightarrow$ Frame discarded.

### D. Sliding Window Flow Control
- Sender maintains a window of size $W$ allowing up to $W$ frames to be sent before receiving an acknowledgement.
- As positive acknowledgements (cumulative ACKs) return from the receiver, the left edge of the sender window slides forward, allowing new frames to enter the transmission pipeline.

### E. Go-Back-N ARQ Protocol
- Receiver accepts **only in-order frames**. If Frame $k$ is lost in transmission:
  1. Subsequent frames in the current window ($k+1, k+2, \dots$) arrive out-of-order and are **discarded** by the receiver.
  2. The receiver re-sends cumulative ACK for the last in-order frame.
  3. Sender timer expires (**TIMEOUT**) for Frame $k$.
  4. The sender **goes back to $k$** and retransmits Frame $k$ and all subsequent unacknowledged frames currently in flight.

### F. NS2 Performance Evaluation Formulas
- **Packet Loss:**
  $$\text{Packet Loss} = \text{Sent Packets} - \text{Received Packets}$$
- **Packet Delivery Ratio (PDR):**
  $$\text{PDR (\%)} = \left(\frac{\text{Received Packets}}{\text{Sent Packets}}\right) \times 100$$
- **Throughput:**
  $$\text{Throughput (Mbps)} = \frac{\text{Total Received Data Bits}}{\text{Simulation Duration (seconds)} \times 10^6}$$
- **Average End-to-End Delay:**
  $$\text{Average Delay (ms)} = \frac{\sum (T_{\text{receive}} - T_{\text{send}})}{\text{Number of Received Packets}} \times 1000$$

---

## 7. Laboratory Demonstration Guide (For Tomorrow's Exam/Viva)

When presenting to lab evaluators, follow this recommended 5-minute walkthrough:

### Step 1: System Boot & Dashboard (30 Seconds)
1. Start the Java backend using `java -jar target\smartcampus-network-1.0.0.jar`.
2. Open `http://localhost:8081` in Chrome/Edge.
3. Show the top status pill: `● Backend Online (Spring Boot)`.
4. Point out the **Dashboard** cards showing 13 active campus devices, online status, throughput, and the 5-department architecture.

### Step 2: Dijkstra Routing (60 Seconds)
1. Navigate to **Dijkstra Routing** tab.
2. Click the preset: **CS ➜ Server Room**.
3. Click **Find Shortest Path**.
4. Highlight to the examiner:
   - Dynamic path: `Computer Science ➜ CS Access Switch ➜ Server Switch ➜ Server Room`.
   - Total Metric Cost: **6**.
   - Hop-by-hop breakdown table with individual link costs.
   - Nodes highlighted in the **Network Topology** tab!
5. Demonstrate edge cases: select `Computer Science` as both source and destination (returns 0 cost) or click **Toggle Status** in Device Monitoring to demonstrate link failure awareness.

### Step 3: Framing & CRC Error Detection (90 Seconds)
1. Navigate to **Framing & CRC** tab.
2. In the Framing section, click **FLAG + ESC Combined** preset (`HELLO FLAG TEST ESC END`).
3. Click **Generate Byte-Stuffed Frames**.
   - Show how the message was divided into frames and bounded by `FLAG [ ... ] FLAG`.
   - Point out how internal `FLAG` became `ESCFLAG` and `ESC` became `ESCESC`.
4. Click **Receiver De-stuffing & Reconstruct**: Show that the receiver reconstructs the exact original message losslessly.
5. In the CRC section, select preset `100100` and polynomial `1011` ($x^3+x+1$).
6. Click **Calculate CRC & Codeword**:
   - Point out the 3 appended zeros, modulo-2 XOR division trace, remainder `101`, and transmitted codeword `100100101`.
7. Click **Verify Received Codeword**: Receiver reports **PASSED (Remainder 000)**.
8. Click **Corrupt 1 Bit (Simulate Noise)**: The last bit flips (`100100100`).
9. Click **Verify Received Codeword**: Receiver reports **FAILED (Non-zero syndrome 001)**, proving corruption was detected!

### Step 4: Sliding Window & Go-Back-N ARQ (60 Seconds)
1. Navigate to **Sliding Window** tab.
2. Click **Start Simulation**, then click **Step Next ⏩** or **Auto Play ▶**.
3. Point out the sender window brackets moving across the frame buffer as cumulative ACKs return.
4. Navigate to **Go-Back-N ARQ** tab.
5. Set `Lost Frame` to **Frame 3** and click **Start GBN Simulation**.
6. Step through:
   - Frames 1 and 2 are ACKed.
   - Frame 3 is marked **LOST** (red).
   - Subsequent Frame 4 arrives out-of-order and is **DISCARDED** (yellow).
   - Sender times out and retransmits Frame 3 and Frame 4.
   - Both frames are accepted in-order by the receiver!

### Step 5: Wireshark & NS2 Performance (60 Seconds)
1. Navigate to **Wireshark** tab.
2. Click **Generate Campus Traffic**: Displays simulated campus ICMP, DNS, TCP handshake (SYN, SYN-ACK, ACK), and HTTP traffic with protocol chips.
3. Click **Load Sample Capture**: Demonstrates CSV import capability from real Wireshark exports with distinct badge `[Data Source: Imported Wireshark CSV Capture]`.
4. Navigate to **NS2 Performance** tab.
5. Click **Run NS2 Demonstration**: Evaluates throughput (0.78 Mbps), PDR (99.36%), and average delay (9.53 ms).
6. Show the interactive Canvas throughput curve chart and the **Mathematical Formulas & Derivations** panel.
7. Click **View NS2 TCL Script**: Displays `campus_simulation.tcl` with execution instructions for lab viva!

---

## 8. Wireshark Capture Instructions

To capture live traffic from campus machines using Wireshark:
1. Open Wireshark and choose your network interface.
2. In the display filter bar, enter:
   ```text
   ip.addr == 192.168.1.10 || icmp || tcp.port == 8081
   ```
3. Generate traffic in the application (e.g. click routing or ping tests).
4. Stop the capture in Wireshark.
5. Export to CSV: **File ➜ Export Packet Dissections ➜ As CSV...**
6. In SCNM, open the Wireshark tab, click **Import Wireshark CSV**, and paste the CSV text to inspect and analyze packets.

---

## 9. NS2 TCL Simulation Script Execution

To run the campus network discrete-event simulation in an NS2 environment:
```bash
# Execute the simulation script
ns campus_simulation.tcl

# Output generated:
# 1. campus_simulation.tr   (ASCII trace file containing +, -, r, d event lines)
# 2. campus_simulation.nam  (Network Animator visualization file)

# View network animation (optional)
nam campus_simulation.nam
```
Upload the generated `campus_simulation.tr` into the SCNM dashboard via **Import NS2 Trace (.tr)** for automated metric calculation!

---

## 10. Sample Test Inputs & Expected Outputs

| Module | Test Input | Expected Output |
|---|---|---|
| **Dijkstra** | Source: `CS_DEPT`, Dest: `SERVER_ROOM` | Path: `CS -> CS Switch -> Server Switch -> Server Room`, Cost: `6` |
| **Dijkstra** | Source: `CS_DEPT`, Dest: `CS_DEPT` | Path: `Computer Science`, Cost: `0` |
| **Framing** | Message: `HELLO FLAG WORLD`, FrameSize: 10 | Frame 1: `FLAG [ HELLO ESCFLAG ] FLAG`, Frame 2: `FLAG [  WORLD ] FLAG` |
| **CRC Calc** | Data: `100100`, Poly: `1011` | Remainder: `101`, Codeword: `100100101` |
| **CRC Verify** | Codeword: `100100101`, Poly: `1011` | Syndrome: `000`, Data Integrity: `PASSED` |
| **CRC Corrupt** | Codeword: `100100100`, Poly: `1011` | Syndrome: `001`, Data Integrity: `FAILED (Bit Error Detected)` |
| **Sliding Window**| Frames: 6, Window: 3 | 19 sequential transmission and window advancement steps |
| **Go-Back-N** | Frames: 8, Window: 4, Lost: 3 | Out-of-order frames discarded; retransmits from Frame 3; all 8 frames ACKed |
| **NS2 Metrics** | Demonstration Mode Trace | Throughput: `0.78 Mbps`, PDR: `99.36%`, Avg Delay: `9.53 ms` |

---

## 11. External Tools Integration Summary

- **Fully Self-Contained Built-in Functionality:**
  - Campus topology graph, device ping/status monitoring, Dijkstra shortest path routing, byte stuffing framing & de-stuffing, CRC modulo-2 division & noise injection, sliding window flow control, and Go-Back-N ARQ error recovery.
- **Tools with Working Import & Demonstration Workflows:**
  - **Wireshark:** Supports live traffic generation and CSV dissection import; includes step-by-step campus packet capture instructions and sample capture dataset.
  - **NS2:** Includes an authentic `campus_simulation.tcl` script, trace parsing engine for `.tr` files, demonstration mode with 200 synthesized packets, and mathematical formula verifier.
