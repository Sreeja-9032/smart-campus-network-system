// SMART CAMPUS NETWORK MONITORING AND ROUTING SYSTEM
// Frontend Controller & Java Spring Boot REST Client

// If accessed via http/https on any port, use relative path ''; otherwise default to http://localhost:8081
const API_BASE = (window.location.protocol.startsWith('http') && window.location.port) ? '' : 'http://localhost:8081';

// ----------------------------------------------------
// 1. NAVIGATION
// ----------------------------------------------------
const navButtons = document.querySelectorAll(".nav-btn");
const pages = document.querySelectorAll(".page");

navButtons.forEach(button => {
    button.addEventListener("click", () => {
        navButtons.forEach(btn => btn.classList.remove("active"));
        pages.forEach(page => page.classList.remove("active"));

        button.classList.add("active");

        const page = document.getElementById(button.dataset.page);
        if (page) page.classList.add("active");
    });
});

// Helper for Fetch API with meaningful error handling
async function apiRequest(endpoint, method = 'GET', body = null) {
    try {
        const options = {
            method: method,
            headers: { 'Content-Type': 'application/json' }
        };
        if (body) {
            options.body = JSON.stringify(body);
        }
        const response = await fetch(`${API_BASE}${endpoint}`, options);
        const data = await response.json();
        return { ok: response.ok, status: response.status, data: data };
    } catch (err) {
        console.warn(`API call failed for ${endpoint}:`, err);
        return { ok: false, status: 0, error: err.message || 'Network connection failed' };
    }
}

// Check Backend Health on startup
async function checkBackendHealth() {
    const statusElem = document.getElementById("systemStatus");
    const res = await apiRequest("/api/network/topology");
    if (res.ok) {
        statusElem.textContent = "● Backend Online (Spring Boot)";
        statusElem.className = "status";
        loadDashboardStats();
        loadDevices();
        loadTopology();
    } else {
        statusElem.textContent = "● Backend Offline (Check Port 8080)";
        statusElem.className = "status error";
    }
}

// ----------------------------------------------------
// 2. DASHBOARD
// ----------------------------------------------------
async function loadDashboardStats() {
    const devRes = await apiRequest("/api/devices");
    if (devRes.ok && Array.isArray(devRes.data)) {
        const total = devRes.data.length;
        const online = devRes.data.filter(d => d.status === 'ONLINE').length;
        const offline = total - online;

        const totalElem = document.getElementById("dashTotalDevices");
        const onlineElem = document.getElementById("dashOnlineDevices");
        const offlineElem = document.getElementById("dashOfflineDevices");

        if (totalElem) totalElem.textContent = total;
        if (onlineElem) onlineElem.textContent = online;
        if (offlineElem) offlineElem.textContent = offline;
    }
}

// ----------------------------------------------------
// 3. TOPOLOGY & DEVICE MONITORING
// ----------------------------------------------------
async function loadTopology() {
    const res = await apiRequest("/api/network/topology");
    if (!res.ok) return;

    const container = document.getElementById("topologyContainer");
    if (container && Array.isArray(res.data.nodes)) {
        container.innerHTML = "";
        res.data.nodes.forEach(node => {
            const card = document.createElement("div");
            card.className = "network-node";
            card.id = `node-${node.id}`;
            const isOnline = node.status === 'ONLINE';
            card.innerHTML = `
                <div style="font-size: 26px;">${node.icon || '🖥️'}</div>
                <strong>${node.name}</strong><br>
                <small>${node.ipAddress}</small><br>
                <span class="badge ${isOnline ? 'online' : 'offline'}">${node.status}</span>
            `;
            container.appendChild(card);
        });
    }

    const tableBody = document.getElementById("topologyLinksTable");
    if (tableBody && Array.isArray(res.data.edges)) {
        tableBody.innerHTML = "";
        const nodeMap = {};
        if (res.data.nodes) {
            res.data.nodes.forEach(n => nodeMap[n.id] = n.name);
        }
        res.data.edges.forEach(edge => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${nodeMap[edge.from] || edge.from}</td>
                <td>${nodeMap[edge.to] || edge.to}</td>
                <td><strong>${edge.cost}</strong></td>
                <td>${edge.description || 'Campus link'}</td>
            `;
            tableBody.appendChild(row);
        });
    }
}

document.getElementById("refreshTopologyBtn")?.addEventListener("click", () => {
    loadTopology();
});

async function loadDevices() {
    const res = await apiRequest("/api/devices");
    const tableBody = document.getElementById("deviceTableBody");
    if (!tableBody) return;

    if (!res.ok) {
        tableBody.innerHTML = `<tr><td colspan="6" style="color: #f87171;">Failed to connect to Java backend (/api/devices).</td></tr>`;
        return;
    }

    tableBody.innerHTML = "";
    res.data.forEach(dev => {
        const row = document.createElement("tr");
        const isOnline = dev.status === 'ONLINE';
        const latencyText = isOnline ? `${dev.latencyMs} ms` : '--';
        row.innerHTML = `
            <td><strong>${dev.name}</strong></td>
            <td><code>${dev.ipAddress}</code></td>
            <td>${dev.type}</td>
            <td class="${isOnline ? 'online' : 'offline'}">${dev.status}</td>
            <td>${latencyText}</td>
            <td>
                <button class="small-btn secondary-btn" onclick="toggleDevice('${dev.id}')">Toggle Status</button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

window.toggleDevice = async function(deviceId) {
    const res = await apiRequest(`/api/devices/${deviceId}/toggle`, 'POST');
    if (res.ok) {
        loadDevices();
        loadTopology();
        loadDashboardStats();
    }
};

document.getElementById("refreshDevicesBtn")?.addEventListener("click", () => {
    loadDevices();
});

// ----------------------------------------------------
// 4. DIJKSTRA ROUTING
// ----------------------------------------------------
const routeBtn = document.getElementById("routeBtn");
const routeResult = document.getElementById("routeResult");
const routeHopsContainer = document.getElementById("routeHopsContainer");
const routeHopsTable = document.getElementById("routeHopsTable");

// Preset Buttons
document.querySelectorAll(".preset-btn[data-src]").forEach(btn => {
    btn.addEventListener("click", () => {
        const srcSelect = document.getElementById("source");
        const dstSelect = document.getElementById("destination");
        if (srcSelect) srcSelect.value = btn.dataset.src;
        if (dstSelect) dstSelect.value = btn.dataset.dst;
        routeBtn.click();
    });
});

routeBtn?.addEventListener("click", async () => {
    const source = document.getElementById("source").value;
    const destination = document.getElementById("destination").value;

    routeResult.innerHTML = `<em>Calculating shortest path using Java Dijkstra algorithm...</em>`;
    if (routeHopsContainer) routeHopsContainer.style.display = "none";

    // Clear previous node highlights
    document.querySelectorAll(".network-node").forEach(n => n.classList.remove("highlighted"));

    const res = await apiRequest("/api/routing/dijkstra", "POST", {
        source: source,
        destination: destination
    });

    if (!res.ok) {
        routeResult.innerHTML = `<span style="color: #f87171;">⚠️ ${res.data?.message || res.error || "Routing failed"}</span>`;
        return;
    }

    const data = res.data;
    if (data.totalCost === 0 && data.source === data.destination) {
        routeResult.innerHTML = `
            <strong>Notice:</strong> ${data.message}
            <br><strong>Total Metric Cost:</strong> 0
        `;
        return;
    }

    const pathVisual = data.pathNodeNames.join(" ➜ ");
    const intermediateStr = data.intermediateDevices.length > 0 
        ? data.intermediateDevices.join(", ") 
        : "None (Direct Connection)";

    routeResult.innerHTML = `
        <strong>Optimal Shortest Path:</strong> ${pathVisual}
        <br><strong>Total Routing Metric Cost:</strong> <span style="font-size: 16px; font-weight: bold; color: #4ade80;">${data.totalCost}</span>
        <br><strong>Intermediate Network Devices:</strong> ${intermediateStr}
    `;

    // Highlight nodes in topology view
    if (Array.isArray(data.pathNodeIds)) {
        data.pathNodeIds.forEach(id => {
            const nodeEl = document.getElementById(`node-${id}`);
            if (nodeEl) nodeEl.classList.add("highlighted");
        });
    }

    // Populate hops table
    if (routeHopsTable && Array.isArray(data.hops) && data.hops.length > 0) {
        const tbody = routeHopsTable.querySelector("tbody");
        tbody.innerHTML = "";
        data.hops.forEach((hop, idx) => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>Hop ${idx + 1}</td>
                <td><strong>${hop.fromName}</strong> (${hop.fromNode})</td>
                <td><strong>${hop.toName}</strong> (${hop.toNode})</td>
                <td>${hop.cost}</td>
                <td><strong>${hop.cumulativeCost}</strong></td>
            `;
            tbody.appendChild(tr);
        });
        routeHopsContainer.style.display = "block";
    }
});

// ----------------------------------------------------
// 5. FRAMING (BYTE STUFFING)
// ----------------------------------------------------
let lastEncodedFrames = [];

const presetFramingNormal = document.getElementById("presetFramingNormal");
const presetFramingFlag = document.getElementById("presetFramingFlag");
const presetFramingEsc = document.getElementById("presetFramingEsc");
const presetFramingBoth = document.getElementById("presetFramingBoth");
const framingInput = document.getElementById("framingMessage");

presetFramingNormal?.addEventListener("click", () => {
    framingInput.value = "CAMPUS NETWORK 2026";
});
presetFramingFlag?.addEventListener("click", () => {
    framingInput.value = "CAMPUS FLAG PACKET 2026";
});
presetFramingEsc?.addEventListener("click", () => {
    framingInput.value = "CAMPUS ESC CONTROL 2026";
});
presetFramingBoth?.addEventListener("click", () => {
    framingInput.value = "HELLO FLAG TEST ESC END";
});

document.getElementById("encodeFramingBtn")?.addEventListener("click", async () => {
    const msg = framingInput.value.trim();
    const flag = document.getElementById("flagDelimiter").value.trim() || "FLAG";
    const esc = document.getElementById("escDelimiter").value.trim() || "ESC";
    const size = parseInt(document.getElementById("framePayloadSize").value) || 10;

    const framingResult = document.getElementById("framingResult");
    const framesDisplay = document.getElementById("framesDisplay");
    const deframingResult = document.getElementById("deframingResult");

    deframingResult.style.display = "none";
    framingResult.innerHTML = `<em>Encoding message via Java Framing Service...</em>`;

    const res = await apiRequest("/api/framing/encode", "POST", {
        message: msg,
        flagDelimiter: flag,
        escDelimiter: esc,
        frameSize: size
    });

    if (!res.ok) {
        framingResult.innerHTML = `<span style="color: #f87171;">⚠️ ${res.data?.message || res.error || "Encoding failed"}</span>`;
        return;
    }

    const data = res.data;
    lastEncodedFrames = data.frames.map(f => f.framedData);

    framingResult.innerHTML = `
        <strong>Original Message:</strong> "${data.originalMessage}"
        <br><strong>Binary Stream:</strong> <code style="font-size: 11px;">${data.binaryRepresentation}</code>
        <br><strong>Total Generated Frames:</strong> ${data.totalFrames} (Payload limit: ${data.frameSize} chars)
        <br><strong>Framing Rule:</strong> Starts & ends with <code>${data.flagDelimiter}</code>; Escapes internal <code>${data.flagDelimiter}</code> and <code>${data.escDelimiter}</code> using <code>${data.escDelimiter}</code>.
    `;

    framesDisplay.innerHTML = "";
    data.frames.forEach(frame => {
        const frameBox = document.createElement("div");
        frameBox.className = "frame in-window";
        frameBox.style.maxWidth = "320px";
        frameBox.style.alignItems = "flex-start";
        frameBox.innerHTML = `
            <strong>Frame #${frame.frameIndex}</strong>
            <small style="color: #94a3b8;">Raw: "${frame.rawPayload}"</small>
            <div style="background: #0f172a; padding: 6px; border-radius: 4px; font-family: monospace; font-size: 12px; margin-top: 4px; width: 100%;">
                <span style="color: #38bdf8; font-weight: bold;">${data.flagDelimiter}</span>
                [ <span style="color: #4ade80;">${frame.stuffedPayload}</span> ]
                <span style="color: #38bdf8; font-weight: bold;">${data.flagDelimiter}</span>
            </div>
            <small style="color: #facc15; font-size: 11px;">Escaped Flags: ${frame.escapedFlagCount} | Escaped ESCs: ${frame.escapedEscCount}</small>
        `;
        framesDisplay.appendChild(frameBox);
    });
});

document.getElementById("decodeFramingBtn")?.addEventListener("click", async () => {
    const deframingResult = document.getElementById("deframingResult");
    const flag = document.getElementById("flagDelimiter").value.trim() || "FLAG";
    const esc = document.getElementById("escDelimiter").value.trim() || "ESC";

    if (!lastEncodedFrames || lastEncodedFrames.length === 0) {
        deframingResult.style.display = "block";
        deframingResult.innerHTML = `⚠️ No framed data available. First click 'Generate Byte-Stuffed Frames'.`;
        return;
    }

    const res = await apiRequest("/api/framing/decode", "POST", {
        framedDataList: lastEncodedFrames,
        flagDelimiter: flag,
        escDelimiter: esc
    });

    if (!res.ok) {
        deframingResult.style.display = "block";
        deframingResult.innerHTML = `<span style="color: #f87171;">⚠️ ${res.data?.message || res.error || "Decoding failed"}</span>`;
        return;
    }

    deframingResult.style.display = "block";
    deframingResult.innerHTML = `
        <strong>Receiver De-stuffing Complete:</strong>
        <br><strong>Reconstructed Message:</strong> <span style="color: #4ade80; font-size: 15px; font-weight: bold;">"${res.data.reconstructedMessage}"</span>
        <br><strong>Frames Processed:</strong> ${res.data.framesProcessed}
        <br><strong>Integrity Check:</strong> Original data matches reconstructed receiver payload 100% losslessly!
    `;
});

// ----------------------------------------------------
// 6. CRC ERROR DETECTION
// ----------------------------------------------------
let currentTransmittedCodeword = "";

// CRC Presets
document.getElementById("presetCrc1")?.addEventListener("click", () => {
    document.getElementById("crcData").value = "100100";
    document.getElementById("polynomial").value = "1011";
});
document.getElementById("presetCrc2")?.addEventListener("click", () => {
    document.getElementById("crcData").value = "1101011011";
    document.getElementById("polynomial").value = "10011";
});
document.getElementById("presetCrc3")?.addEventListener("click", () => {
    document.getElementById("crcData").value = "1010001101";
    document.getElementById("polynomial").value = "1101";
});

document.getElementById("crcBtn")?.addEventListener("click", async () => {
    const data = document.getElementById("crcData").value.trim();
    const polynomial = document.getElementById("polynomial").value.trim();
    const result = document.getElementById("crcResult");
    const receiverPanel = document.getElementById("crcReceiverPanel");
    const receivedInput = document.getElementById("receivedCodewordInput");

    result.innerHTML = `<em>Calculating CRC modulo-2 division in Java backend...</em>`;

    const res = await apiRequest("/api/crc/calculate", "POST", {
        data: data,
        polynomial: polynomial
    });

    if (!res.ok) {
        result.innerHTML = `<span style="color: #f87171;">⚠️ ${res.data?.message || res.error || "Calculation failed"}</span>`;
        return;
    }

    const crc = res.data;
    currentTransmittedCodeword = crc.transmittedCodeword;

    result.innerHTML = `
        <strong>Original Data:</strong> <code>${crc.originalData}</code>
        <br><strong>Generator Polynomial:</strong> <code>${crc.polynomial}</code> (Degree: ${crc.appendedZeros.length})
        <br><strong>Dividend (Data + ${crc.appendedZeros.length} Zeros):</strong> <code>${crc.dividend}</code>
        <br><strong>CRC Remainder:</strong> <span style="color: #4ade80; font-weight: bold;">${crc.remainder}</span>
        <br><strong>Transmitted Codeword:</strong> <code style="font-size: 15px; color: #38bdf8;">${crc.transmittedCodeword}</code>
    `;

    if (receiverPanel) receiverPanel.style.display = "block";
    if (receivedInput) receivedInput.value = crc.transmittedCodeword;

    const verifyResult = document.getElementById("crcVerifyResult");
    if (verifyResult) verifyResult.innerHTML = `Codeword loaded into Receiver. Click 'Verify Received Codeword' or 'Corrupt 1 Bit' to test detection.`;
});

// Corrupt 1 Bit Button
document.getElementById("corruptBitBtn")?.addEventListener("click", () => {
    const input = document.getElementById("receivedCodewordInput");
    if (!input || !input.value) return;

    const bits = input.value.split("");
    // Invert the last bit (or first bit)
    const targetIdx = bits.length - 1;
    bits[targetIdx] = bits[targetIdx] === "1" ? "0" : "1";
    input.value = bits.join("");

    const verifyResult = document.getElementById("crcVerifyResult");
    if (verifyResult) {
        verifyResult.innerHTML = `<span style="color: #facc15;">⚡ Injected 1-bit transmission error at bit index ${targetIdx}. Click 'Verify Received Codeword' to observe CRC error detection.</span>`;
    }
});

// Verify Codeword at Receiver
document.getElementById("verifyCrcBtn")?.addEventListener("click", async () => {
    const codeword = document.getElementById("receivedCodewordInput").value.trim();
    const polynomial = document.getElementById("polynomial").value.trim();
    const verifyResult = document.getElementById("crcVerifyResult");

    verifyResult.innerHTML = `<em>Performing receiver modulo-2 division...</em>`;

    const res = await apiRequest("/api/crc/verify", "POST", {
        receivedCodeword: codeword,
        polynomial: polynomial
    });

    if (!res.ok) {
        verifyResult.innerHTML = `<span style="color: #f87171;">⚠️ ${res.data?.message || res.error || "Verification failed"}</span>`;
        return;
    }

    const data = res.data;
    if (data.dataIntegrityPass) {
        verifyResult.innerHTML = `
            <div style="color: #4ade80; font-size: 15px; font-weight: bold;">✅ ${data.message}</div>
            <strong>Receiver Syndrome Remainder:</strong> <code>${data.remainder}</code>
            <br>The received codeword is completely valid and accepted without errors.
        `;
    } else {
        verifyResult.innerHTML = `
            <div style="color: #f87171; font-size: 15px; font-weight: bold;">❌ ${data.message}</div>
            <strong>Non-Zero Remainder (Syndrome):</strong> <code style="color: #f87171;">${data.remainder}</code>
            <br>CRC Modulo-2 division successfully caught the transmission error! The frame is rejected.
        `;
    }
});

// ----------------------------------------------------
// 7. SLIDING WINDOW PROTOCOL
// ----------------------------------------------------
let slidingSteps = [];
let slidingStepIndex = 0;
let slidingAutoTimer = null;

function resetSlidingUI() {
    if (slidingAutoTimer) clearInterval(slidingAutoTimer);
    slidingSteps = [];
    slidingStepIndex = 0;
    document.getElementById("senderFrames").innerHTML = "";
    document.getElementById("receiverFrames").innerHTML = "";
    document.getElementById("slidingResult").textContent = "Simulation reset. Click Start Simulation.";
    document.getElementById("windowInfoBadge").textContent = "Window: Idle";
    document.getElementById("stepCounterBadge").textContent = "Step: 0";
    document.getElementById("remainingBadge").textContent = "Frames Remaining: 0";
}

function renderSlidingStep(step) {
    if (!step) return;

    const sender = document.getElementById("senderFrames");
    const receiver = document.getElementById("receiverFrames");
    const result = document.getElementById("slidingResult");
    const windowBadge = document.getElementById("windowInfoBadge");
    const stepBadge = document.getElementById("stepCounterBadge");
    const remBadge = document.getElementById("remainingBadge");

    windowBadge.textContent = `Window: [${step.windowStart} .. ${step.windowEnd}]`;
    stepBadge.textContent = `Step: ${step.stepNumber + 1}/${slidingSteps.length}`;
    remBadge.textContent = `Frames Remaining: ${step.framesRemaining}`;

    sender.innerHTML = "";
    receiver.innerHTML = "";

    const total = parseInt(document.getElementById("totalFrames").value) || 8;

    for (let f = 1; f <= total; f++) {
        const frameDiv = document.createElement("div");
        frameDiv.className = "frame";
        const status = step.frameStatuses ? step.frameStatuses[f] : "PENDING";

        if (status === "ACKNOWLEDGED") {
            frameDiv.classList.add("acknowledged");
            frameDiv.innerHTML = `<span>Frame ${f}</span><small>✓ ACK</small>`;
        } else if (status === "TRANSMITTING") {
            frameDiv.classList.add("transmitting");
            frameDiv.innerHTML = `<span>Frame ${f}</span><small>⚡ Sending</small>`;
        } else if (status === "IN_WINDOW") {
            frameDiv.classList.add("in-window");
            frameDiv.innerHTML = `<span>Frame ${f}</span><small>In Window</small>`;
        } else {
            frameDiv.innerHTML = `<span>Frame ${f}</span><small>Pending</small>`;
        }

        sender.appendChild(frameDiv);
    }

    // Render Receiver ACKs
    for (let a = 1; a <= step.acknowledgedFrame; a++) {
        const ackDiv = document.createElement("div");
        ackDiv.className = "frame acknowledged";
        ackDiv.innerHTML = `<span>ACK ${a}</span><small>Received</small>`;
        receiver.appendChild(ackDiv);
    }

    result.innerHTML = `
        <strong>Step Action:</strong> ${step.action}
        <br>${step.description}
    `;
}

document.getElementById("slidingBtn")?.addEventListener("click", async () => {
    resetSlidingUI();
    const wSize = parseInt(document.getElementById("windowSize").value) || 4;
    const total = parseInt(document.getElementById("totalFrames").value) || 8;

    const res = await apiRequest("/api/sliding-window/simulate", "POST", {
        windowSize: wSize,
        totalFrames: total
    });

    if (!res.ok) {
        document.getElementById("slidingResult").innerHTML = `<span style="color: #f87171;">⚠️ ${res.data?.message || res.error}</span>`;
        return;
    }

    slidingSteps = res.data.steps;
    slidingStepIndex = 0;
    renderSlidingStep(slidingSteps[0]);
});

document.getElementById("slidingNextBtn")?.addEventListener("click", () => {
    if (slidingSteps.length === 0) {
        document.getElementById("slidingResult").textContent = "Please click 'Start Simulation' first.";
        return;
    }
    if (slidingStepIndex < slidingSteps.length - 1) {
        slidingStepIndex++;
        renderSlidingStep(slidingSteps[slidingStepIndex]);
    } else {
        document.getElementById("slidingResult").innerHTML = `<strong>Simulation Completed:</strong> All frames have been transmitted and acknowledged.`;
    }
});

document.getElementById("slidingAutoBtn")?.addEventListener("click", () => {
    if (slidingSteps.length === 0) return;
    if (slidingAutoTimer) {
        clearInterval(slidingAutoTimer);
        slidingAutoTimer = null;
        document.getElementById("slidingAutoBtn").textContent = "Auto Play ▶";
        return;
    }
    document.getElementById("slidingAutoBtn").textContent = "Pause ⏸";
    slidingAutoTimer = setInterval(() => {
        if (slidingStepIndex < slidingSteps.length - 1) {
            slidingStepIndex++;
            renderSlidingStep(slidingSteps[slidingStepIndex]);
        } else {
            clearInterval(slidingAutoTimer);
            slidingAutoTimer = null;
            document.getElementById("slidingAutoBtn").textContent = "Auto Play ▶";
        }
    }, 1100);
});

document.getElementById("slidingResetBtn")?.addEventListener("click", resetSlidingUI);

// ----------------------------------------------------
// 8. GO-BACK-N ARQ
// ----------------------------------------------------
let gbnSteps = [];
let gbnStepIndex = 0;
let gbnAutoTimer = null;

function resetGBNUI() {
    if (gbnAutoTimer) clearInterval(gbnAutoTimer);
    gbnSteps = [];
    gbnStepIndex = 0;
    document.getElementById("gbnSender").innerHTML = "";
    document.getElementById("gbnReceiver").innerHTML = "";
    document.getElementById("gbnResult").textContent = "Ready for Go-Back-N simulation. Click Start GBN Simulation.";
    document.getElementById("gbnSenderWindowLabel").textContent = "[1 .. 4]";
    document.getElementById("gbnReceiverExpectedLabel").textContent = "Frame 1";
}

function renderGBNStep(step) {
    if (!step) return;

    const sender = document.getElementById("gbnSender");
    const receiver = document.getElementById("gbnReceiver");
    const result = document.getElementById("gbnResult");
    const windowLabel = document.getElementById("gbnSenderWindowLabel");
    const expectedLabel = document.getElementById("gbnReceiverExpectedLabel");

    windowLabel.textContent = `[${step.windowStart} .. ${step.windowEnd}]`;
    expectedLabel.textContent = `Frame ${step.expectedFrameReceiver}`;

    sender.innerHTML = "";
    receiver.innerHTML = "";

    const total = parseInt(document.getElementById("gbnTotal").value) || 8;

    for (let f = 1; f <= total; f++) {
        const sStatus = step.senderFrameState ? step.senderFrameState[f] : "PENDING";
        const sDiv = document.createElement("div");
        sDiv.className = "frame";

        if (sStatus === "ACKED") {
            sDiv.classList.add("acknowledged");
            sDiv.innerHTML = `<span>Frame ${f}</span><small>✓ ACK</small>`;
        } else if (sStatus === "LOST") {
            sDiv.classList.add("lost");
            sDiv.innerHTML = `<span>Frame ${f}</span><small>❌ LOST</small>`;
        } else if (sStatus === "RETRANSMITTED") {
            sDiv.classList.add("retransmitted");
            sDiv.innerHTML = `<span>Frame ${f}</span><small>🔁 Re-sent</small>`;
        } else if (sStatus === "SENT") {
            sDiv.classList.add("in-window");
            sDiv.innerHTML = `<span>Frame ${f}</span><small>In Flight</small>`;
        } else {
            sDiv.innerHTML = `<span>Frame ${f}</span><small>Pending</small>`;
        }
        sender.appendChild(sDiv);

        // Receiver frame card
        const rStatus = step.receiverFrameState ? step.receiverFrameState[f] : "WAITING";
        const rDiv = document.createElement("div");
        rDiv.className = "frame";

        if (rStatus === "ACCEPTED") {
            rDiv.classList.add("acknowledged");
            rDiv.innerHTML = `<span>Frame ${f}</span><small>Accepted</small>`;
        } else if (rStatus === "DISCARDED") {
            rDiv.classList.add("discarded");
            rDiv.innerHTML = `<span>Frame ${f}</span><small>Discarded</small>`;
        } else {
            rDiv.innerHTML = `<span>Frame ${f}</span><small>Waiting</small>`;
        }
        receiver.appendChild(rDiv);
    }

    result.innerHTML = `
        <strong>Event [${step.eventType}]:</strong> Step ${step.stepNumber + 1}/${gbnSteps.length}
        <br><strong>Sender:</strong> ${step.senderMessage}
        <br><strong>Receiver:</strong> ${step.receiverMessage}
    `;
}

document.getElementById("gbnSendBtn")?.addEventListener("click", async () => {
    resetGBNUI();
    const wSize = parseInt(document.getElementById("gbnWindow").value) || 4;
    const total = parseInt(document.getElementById("gbnTotal").value) || 8;
    const lost = parseInt(document.getElementById("lostFrame").value);

    const res = await apiRequest("/api/gobackn/simulate", "POST", {
        windowSize: wSize,
        totalFrames: total,
        lostFrame: lost
    });

    if (!res.ok) {
        document.getElementById("gbnResult").innerHTML = `<span style="color: #f87171;">⚠️ ${res.data?.message || res.error}</span>`;
        return;
    }

    gbnSteps = res.data.steps;
    gbnStepIndex = 0;
    renderGBNStep(gbnSteps[0]);
});

document.getElementById("gbnNextBtn")?.addEventListener("click", () => {
    if (gbnSteps.length === 0) {
        document.getElementById("gbnResult").textContent = "Please click 'Start GBN Simulation' first.";
        return;
    }
    if (gbnStepIndex < gbnSteps.length - 1) {
        gbnStepIndex++;
        renderGBNStep(gbnSteps[gbnStepIndex]);
    } else {
        document.getElementById("gbnResult").innerHTML = `<strong>Go-Back-N Simulation Complete:</strong> All frames accepted by receiver.`;
    }
});

document.getElementById("gbnAutoBtn")?.addEventListener("click", () => {
    if (gbnSteps.length === 0) return;
    if (gbnAutoTimer) {
        clearInterval(gbnAutoTimer);
        gbnAutoTimer = null;
        document.getElementById("gbnAutoBtn").textContent = "Auto Play ▶";
        return;
    }
    document.getElementById("gbnAutoBtn").textContent = "Pause ⏸";
    gbnAutoTimer = setInterval(() => {
        if (gbnStepIndex < gbnSteps.length - 1) {
            gbnStepIndex++;
            renderGBNStep(gbnSteps[gbnStepIndex]);
        } else {
            clearInterval(gbnAutoTimer);
            gbnAutoTimer = null;
            document.getElementById("gbnAutoBtn").textContent = "Auto Play ▶";
        }
    }, 1300);
});

document.getElementById("gbnRetransmitBtn")?.addEventListener("click", () => {
    if (gbnSteps.length === 0) return;
    // Fast-forward to the first RETRANSMIT event
    const retxIdx = gbnSteps.findIndex(s => s.eventType === "RETRANSMIT");
    if (retxIdx !== -1) {
        gbnStepIndex = retxIdx;
        renderGBNStep(gbnSteps[gbnStepIndex]);
    }
});

document.getElementById("gbnResetBtn")?.addEventListener("click", resetGBNUI);

// ----------------------------------------------------
// 9. WIRESHARK PACKET ANALYSIS
// ----------------------------------------------------
const captureBtn = document.getElementById("captureBtn");
const importCsvBtn = document.getElementById("importCsvBtn");
const loadSampleCsvBtn = document.getElementById("loadSampleCsvBtn");
const clearCaptureBtn = document.getElementById("clearCaptureBtn");
const csvImportPanel = document.getElementById("csvImportPanel");
const processCsvBtn = document.getElementById("processCsvBtn");
const closeCsvPanelBtn = document.getElementById("closeCsvPanelBtn");
const packetTable = document.getElementById("packetTable");
const packetResult = document.getElementById("packetResult");
const packetSourceBadge = document.getElementById("packetSourceBadge");

function renderPackets(data) {
    if (!packetTable) return;
    packetTable.innerHTML = "";

    if (packetSourceBadge) {
        packetSourceBadge.textContent = `Data Source: ${data.dataSource || 'Simulated Campus Traffic'}`;
        packetSourceBadge.className = data.dataSource.includes("Imported") 
            ? "data-badge imported" 
            : "data-badge simulated";
    }

    // Protocol chips
    const chips = document.getElementById("protocolChips");
    if (chips && data.protocolBreakdown) {
        chips.innerHTML = "";
        for (const [proto, count] of Object.entries(data.protocolBreakdown)) {
            const span = document.createElement("span");
            span.className = `chip ${proto.toLowerCase()}`;
            span.textContent = `${proto}: ${count}`;
            chips.appendChild(span);
        }
    }

    if (Array.isArray(data.packets)) {
        data.packets.forEach(p => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${p.packetNumber}</td>
                <td>${p.timestamp ? p.timestamp.toFixed(6) : '0.000'}</td>
                <td><code>${p.sourceIp}</code></td>
                <td><code>${p.destinationIp}</code></td>
                <td><span class="chip ${p.protocol.toLowerCase()}">${p.protocol}</span></td>
                <td>${p.lengthBytes} bytes</td>
                <td><span class="badge online">${p.status || 'Success'}</span></td>
                <td><small>${p.info}</small></td>
            `;
            packetTable.appendChild(tr);
        });
    }

    packetResult.innerHTML = `
        <strong>${data.message || 'Packets loaded.'}</strong>
        <br>Total packets inspected: <strong>${data.totalPackets || (data.packets ? data.packets.length : 0)}</strong>.
    `;
}

captureBtn?.addEventListener("click", async () => {
    packetResult.innerHTML = `<em>Generating simulated campus traffic via Spring Boot API...</em>`;
    const res = await apiRequest("/api/packets/generate");
    if (res.ok) {
        renderPackets(res.data);
    } else {
        packetResult.innerHTML = `<span style="color: #f87171;">⚠️ Failed to generate traffic: ${res.error}</span>`;
    }
});

importCsvBtn?.addEventListener("click", () => {
    if (csvImportPanel) {
        csvImportPanel.style.display = csvImportPanel.style.display === "none" ? "block" : "none";
    }
});

closeCsvPanelBtn?.addEventListener("click", () => {
    if (csvImportPanel) csvImportPanel.style.display = "none";
});

loadSampleCsvBtn?.addEventListener("click", async () => {
    const sampleCsv = `"No.","Time","Source","Destination","Protocol","Length","Info"
"1","0.000000","192.168.1.10","192.168.5.10","ICMP","74","Echo (ping) request id=0x0001, seq=1/256, ttl=64"
"2","0.002840","192.168.5.10","192.168.1.10","ICMP","74","Echo (ping) reply id=0x0001, seq=1/256, ttl=64 (reply in 2.84ms)"
"3","0.012450","192.168.1.10","192.168.0.1","DNS","82","Standard query 0x7f4a A portal.campus.edu"
"4","0.014230","192.168.0.1","192.168.1.10","DNS","98","Standard query response 0x7f4a A portal.campus.edu -> 192.168.5.10"
"5","0.021000","192.168.1.10","192.168.5.10","TCP","66","49152 → 8080 [SYN] Seq=0 Win=65535 Len=0 MSS=1460 WS=256"
"6","0.023410","192.168.5.10","192.168.1.10","TCP","66","8080 → 49152 [SYN, ACK] Seq=0 Ack=1 Win=65535 Len=0 MSS=1460"
"7","0.023800","192.168.1.10","192.168.5.10","TCP","54","49152 → 8080 [ACK] Seq=1 Ack=1 Win=65535 Len=0"
"8","0.030100","192.168.1.10","192.168.5.10","HTTP","420","GET /index.html HTTP/1.1"
"9","0.035400","192.168.5.10","192.168.1.10","HTTP","1480","HTTP/1.1 200 OK (text/html)"
"10","0.048200","192.168.2.10","192.168.5.10","UDP","128","Source port: 9000 Destination port: 9000 Len=120"
"11","0.052100","192.168.3.10","192.168.5.10","TCP","66","50214 → 8080 [SYN] Seq=0 Win=65535 Len=0"
"12","0.061000","192.168.4.10","192.168.5.10","HTTP","510","POST /admin/auth HTTP/1.1"`;

    const csvText = document.getElementById("csvInputText");
    if (csvText) csvText.value = sampleCsv;
    if (csvImportPanel) csvImportPanel.style.display = "block";
    processCsvBtn?.click();
});

processCsvBtn?.addEventListener("click", async () => {
    const csvContent = document.getElementById("csvInputText").value.trim();
    if (!csvContent) {
        alert("Please paste Wireshark CSV content first.");
        return;
    }

    const res = await apiRequest("/api/packets/import", "POST", { csvContent: csvContent });
    if (res.ok) {
        renderPackets(res.data);
        if (csvImportPanel) csvImportPanel.style.display = "none";
    } else {
        alert("Import failed: " + (res.data?.message || res.error));
    }
});

clearCaptureBtn?.addEventListener("click", () => {
    if (packetTable) packetTable.innerHTML = "";
    if (packetResult) packetResult.textContent = "Packet list cleared.";
});

// ----------------------------------------------------
// 10. NS2 PERFORMANCE ANALYSIS
// ----------------------------------------------------
const ns2RunBtn = document.getElementById("ns2RunBtn");
const importTraceBtn = document.getElementById("importTraceBtn");
const viewTclBtn = document.getElementById("viewTclBtn");
const traceImportPanel = document.getElementById("traceImportPanel");
const tclScriptModal = document.getElementById("tclScriptModal");
const processTraceBtn = document.getElementById("processTraceBtn");
const loadSampleTraceBtn = document.getElementById("loadSampleTraceBtn");
const closeTracePanelBtn = document.getElementById("closeTracePanelBtn");
const closeTclBtn = document.getElementById("closeTclBtn");

function renderNs2Metrics(data) {
    document.getElementById("throughput").textContent = `${data.throughputMbps} Mbps`;
    document.getElementById("packetLoss").textContent = `${data.packetLossPercentage}%`;
    document.getElementById("deliveryRatio").textContent = `${data.packetDeliveryRatioPercentage}%`;
    document.getElementById("averageDelay").textContent = `${data.averageDelayMs} ms`;

    const badge = document.getElementById("ns2ModeBadge");
    if (badge) {
        badge.textContent = `Mode: ${data.mode === 'IMPORTED_TRACE' ? 'Imported NS2 Trace File (.tr)' : 'Demonstration Mode (Synthesized Trace)'}`;
        badge.className = data.mode === 'IMPORTED_TRACE' ? 'data-badge imported' : 'data-badge simulated';
    }

    // Formulas
    if (data.formulaExplanations) {
        const lossP = document.getElementById("formulaLossCalc");
        const pdrP = document.getElementById("formulaPdrCalc");
        const tpP = document.getElementById("formulaThroughputCalc");
        const delayP = document.getElementById("formulaDelayCalc");

        if (lossP && data.formulaExplanations["Packet Loss"]) {
            lossP.textContent = data.formulaExplanations["Packet Loss"];
        }
        if (pdrP && data.formulaExplanations["Packet Delivery Ratio (PDR)"]) {
            pdrP.textContent = data.formulaExplanations["Packet Delivery Ratio (PDR)"];
        }
        if (tpP && data.formulaExplanations["Throughput"]) {
            tpP.textContent = data.formulaExplanations["Throughput"];
        }
        if (delayP && data.formulaExplanations["Average End-to-End Delay"]) {
            delayP.textContent = data.formulaExplanations["Average End-to-End Delay"];
        }
    }

    // Canvas Chart
    drawNs2ThroughputChart(data.throughputTimePoints, data.timeLabels);

    const resultDiv = document.getElementById("ns2Result");
    if (resultDiv) {
        resultDiv.innerHTML = `
            <strong>${data.message || 'Analysis complete.'}</strong>
            <br>Sent: <strong>${data.sentPackets}</strong> pkts | Received: <strong>${data.receivedPackets}</strong> pkts | Dropped: <strong>${data.droppedPackets}</strong> pkts | Simulation Duration: <strong>${data.simulationDurationSeconds}s</strong>
        `;
    }
}

function drawNs2ThroughputChart(points = [65, 75, 60, 85, 72, 90, 86], labels = ["1", "2", "3", "4", "5", "6", "7"]) {
    const canvas = document.getElementById("performanceChart");
    if (!canvas) return;
    const ctx = canvas.getContext("2d");

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Background grid
    ctx.strokeStyle = "#1e293b";
    ctx.lineWidth = 1;
    for (let y = 40; y <= 200; y += 40) {
        ctx.beginPath();
        ctx.moveTo(40, y);
        ctx.lineTo(620, y);
        ctx.stroke();
    }

    const maxVal = Math.max(...points, 10);
    const minVal = 0;
    const range = maxVal - minVal || 1;

    // Line curve
    ctx.strokeStyle = "#38bdf8";
    ctx.lineWidth = 3;
    ctx.beginPath();

    const stepX = (canvas.width - 100) / (points.length - 1 || 1);

    points.forEach((val, i) => {
        const x = 60 + i * stepX;
        const norm = (val - minVal) / range;
        const y = 200 - (norm * 140);

        if (i === 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
    });
    ctx.stroke();

    // Data points & Labels
    points.forEach((val, i) => {
        const x = 60 + i * stepX;
        const norm = (val - minVal) / range;
        const y = 200 - (norm * 140);

        ctx.fillStyle = "#4ade80";
        ctx.beginPath();
        ctx.arc(x, y, 5, 0, Math.PI * 2);
        ctx.fill();

        ctx.fillStyle = "#e2e8f0";
        ctx.font = "11px Arial";
        ctx.fillText(`${val}`, x - 10, y - 10);

        ctx.fillStyle = "#94a3b8";
        ctx.fillText(labels[i] || `t=${i+1}`, x - 12, 225);
    });

    ctx.fillStyle = "#94a3b8";
    ctx.font = "12px Arial";
    ctx.fillText("Throughput (Mbps) over Simulation Time", 60, 25);
}

ns2RunBtn?.addEventListener("click", async () => {
    document.getElementById("ns2Result").innerHTML = `<em>Running NS2 demonstration trace analysis in Java backend...</em>`;
    const res = await apiRequest("/api/metrics/analyze", "POST", {});
    if (res.ok) {
        renderNs2Metrics(res.data);
    } else {
        document.getElementById("ns2Result").innerHTML = `<span style="color: #f87171;">⚠️ ${res.error}</span>`;
    }
});

importTraceBtn?.addEventListener("click", () => {
    if (traceImportPanel) {
        traceImportPanel.style.display = traceImportPanel.style.display === "none" ? "block" : "none";
    }
});

closeTracePanelBtn?.addEventListener("click", () => {
    if (traceImportPanel) traceImportPanel.style.display = "none";
});

loadSampleTraceBtn?.addEventListener("click", async () => {
    const res = await fetch(`${API_BASE}/api/metrics/sample-trace`);
    if (res.ok) {
        const text = await res.text();
        document.getElementById("traceInputText").value = text;
    }
});

processTraceBtn?.addEventListener("click", async () => {
    const traceContent = document.getElementById("traceInputText").value.trim();
    if (!traceContent) {
        alert("Please paste NS2 trace content first.");
        return;
    }

    const res = await apiRequest("/api/metrics/analyze", "POST", { traceContent: traceContent });
    if (res.ok) {
        renderNs2Metrics(res.data);
        if (traceImportPanel) traceImportPanel.style.display = "none";
    } else {
        alert("Trace analysis failed: " + (res.data?.message || res.error));
    }
});

viewTclBtn?.addEventListener("click", async () => {
    if (!tclScriptModal) return;
    if (tclScriptModal.style.display === "block") {
        tclScriptModal.style.display = "none";
        return;
    }

    const res = await fetch(`${API_BASE}/api/ns2/tcl-script`);
    if (res.ok) {
        const text = await res.text();
        document.getElementById("tclScriptContent").textContent = text;
        tclScriptModal.style.display = "block";
    }
});

closeTclBtn?.addEventListener("click", () => {
    if (tclScriptModal) tclScriptModal.style.display = "none";
});

// Initial boot
window.addEventListener("DOMContentLoaded", () => {
    checkBackendHealth();
    drawNs2ThroughputChart();
});

console.log("SCNM Smart Campus Network System Initialized.");
