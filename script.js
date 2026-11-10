// SMART CAMPUS NETWORK MONITORING SYSTEM

// 1. NAVIGATION
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


// 2. DIJKSTRA SHORTEST PATH
document.getElementById("routeBtn").addEventListener("click", () => {
    const source = document.getElementById("source").value;
    const destination = document.getElementById("destination").value;
    const result = document.getElementById("routeResult");

    if (source === destination) {
        result.textContent = "Source and destination are the same device.";
        return;
    }

    const graph = {
        Internet: { Router: 2 },
        Router: { Internet: 2, Switch: 3 },
        Switch: { Router: 3, Server: 2, Lab: 4 },
        Server: { Switch: 2, Lab: 5 },
        Lab: { Switch: 4, Server: 5 }
    };

    const distances = {};
    const previous = {};
    const visited = new Set();

    Object.keys(graph).forEach(node => {
        distances[node] = Infinity;
        previous[node] = null;
    });

    distances[source] = 0;

    while (visited.size < Object.keys(graph).length) {
        let current = null;

        for (const node of Object.keys(graph)) {
            if (!visited.has(node) &&
                (current === null || distances[node] < distances[current])) {
                current = node;
            }
        }

        if (current === null || distances[current] === Infinity) break;

        visited.add(current);

        for (const neighbor in graph[current]) {
            const newDistance =
                distances[current] + graph[current][neighbor];

            if (newDistance < distances[neighbor]) {
                distances[neighbor] = newDistance;
                previous[neighbor] = current;
            }
        }
    }

    if (distances[destination] === Infinity) {
        result.textContent = "No route found.";
        return;
    }

    const path = [];
    let current = destination;

    while (current !== null) {
        path.unshift(current);
        current = previous[current];
    }

    result.innerHTML =
        "<strong>Shortest Path:</strong> " + path.join(" → ") +
        "<br><strong>Total Cost:</strong> " + distances[destination];
});


// 3. FRAMING AND CRC
document.getElementById("crcBtn").addEventListener("click", () => {
    const data = document.getElementById("crcData").value.trim();
    const polynomial = document.getElementById("polynomial").value;
    const result = document.getElementById("crcResult");

    if (!/^[01]+$/.test(data)) {
        result.textContent = "Please enter valid binary data (0 and 1 only).";
        return;
    }

    let dividend = data + "0".repeat(polynomial.length - 1);
    let bits = dividend.split("");

    for (let i = 0; i <= bits.length - polynomial.length; i++) {
        if (bits[i] === "1") {
            for (let j = 0; j < polynomial.length; j++) {
                bits[i + j] =
                    String(Number(bits[i + j]) ^ Number(polynomial[j]));
            }
        }
    }

    const crc = bits.slice(-(polynomial.length - 1)).join("");
    const transmitted = data + crc;

    result.innerHTML =
        "<strong>Original Data:</strong> " + data +
        "<br><strong>CRC Remainder:</strong> " + crc +
        "<br><strong>Transmitted Frame:</strong> " + transmitted;
});


// 4. SLIDING WINDOW PROTOCOL
let slidingPosition = 0;

function resetSliding() {
    slidingPosition = 0;
    document.getElementById("senderFrames").innerHTML = "";
    document.getElementById("receiverFrames").innerHTML = "";
    document.getElementById("slidingResult").textContent =
        "Ready to send frames.";
}

function showSlidingFrames() {
    const windowSize = Number(document.getElementById("windowSize").value);
    const total = Number(document.getElementById("totalFrames").value);
    const sender = document.getElementById("senderFrames");
    const receiver = document.getElementById("receiverFrames");
    const result = document.getElementById("slidingResult");

    if (!Number.isInteger(windowSize) || !Number.isInteger(total) ||
        windowSize < 1 || total < 1 || total > 20 ||
        windowSize > 7) {
        result.textContent = "Enter a window size from 1 to 7 and frames from 1 to 20.";
        return;
    }

    if (slidingPosition >= total) {
        result.textContent = "All frames have been transmitted.";
        return;
    }

    sender.innerHTML = "";
    receiver.innerHTML = "";

    const end = Math.min(slidingPosition + windowSize, total);

    for (let i = slidingPosition; i < end; i++) {
        const frame = document.createElement("div");
        frame.className = "frame";
        frame.textContent = "Frame " + (i + 1);
        sender.appendChild(frame);

        const ack = document.createElement("div");
        ack.className = "frame acknowledged";
        ack.textContent = "ACK " + (i + 1);
        receiver.appendChild(ack);
    }

    result.textContent =
        "Frames " + (slidingPosition + 1) + " to " + end +
        " sent and acknowledged.";

    slidingPosition = end;
}

document.getElementById("slidingBtn").addEventListener("click", () => {
    resetSliding();
    showSlidingFrames();
});

document.getElementById("slidingNextBtn").addEventListener("click",
    showSlidingFrames);

document.getElementById("slidingResetBtn").addEventListener("click",
    resetSliding);


// 5. GO-BACK-N ARQ
let gbnFailed = false;
let gbnCompleted = false;

function resetGBN() {
    gbnFailed = false;
    gbnCompleted = false;
    document.getElementById("gbnSender").innerHTML = "";
    document.getElementById("gbnReceiver").innerHTML = "";
    document.getElementById("gbnResult").textContent =
        "Ready for transmission.";
}

function addGBNFrame(container, text, className = "frame") {
    const frame = document.createElement("div");
    frame.className = className;
    frame.textContent = text;
    container.appendChild(frame);
}

document.getElementById("gbnSendBtn").addEventListener("click", () => {
    resetGBN();

    const total = Number(document.getElementById("gbnTotal").value);
    const windowSize = Number(document.getElementById("gbnWindow").value);
    const lost = Number(document.getElementById("lostFrame").value);
    const sender = document.getElementById("gbnSender");
    const receiver = document.getElementById("gbnReceiver");
    const result = document.getElementById("gbnResult");

    if (!Number.isInteger(total) || !Number.isInteger(windowSize) ||
        total < 1 || total > 20 || windowSize < 1 || windowSize > 7) {
        result.textContent = "Enter valid frame and window sizes.";
        return;
    }

    for (let i = 1; i <= Math.min(total, windowSize); i++) {
        if (i === lost) {
            addGBNFrame(sender, "Frame " + i + " LOST", "frame lost");
            addGBNFrame(receiver, "Waiting for Frame " + i, "frame lost");
            gbnFailed = true;
            break;
        }

        addGBNFrame(sender, "Frame " + i);
        addGBNFrame(receiver, "ACK " + i, "frame acknowledged");
    }

    if (gbnFailed) {
        result.textContent =
            "Frame " + lost + " was lost. Later frames are not accepted. Retransmission is required.";
    } else {
        result.textContent = "Initial window transmitted successfully.";
        gbnCompleted = true;
    }
});

document.getElementById("gbnRetransmitBtn").addEventListener("click", () => {
    const total = Number(document.getElementById("gbnTotal").value);
    const windowSize = Number(document.getElementById("gbnWindow").value);
    const lost = Number(document.getElementById("lostFrame").value);
    const sender = document.getElementById("gbnSender");
    const receiver = document.getElementById("gbnReceiver");
    const result = document.getElementById("gbnResult");

    if (!gbnFailed && !gbnCompleted) {
        result.textContent = "First click Send Frames.";
        return;
    }

    if (gbnCompleted) {
        result.textContent = "The initial transmission was successful.";
        return;
    }

    for (let i = lost; i <= Math.min(total, lost + windowSize - 1); i++) {
        addGBNFrame(sender, "Retransmitted " + i);
        addGBNFrame(receiver, "ACK " + i, "frame acknowledged");
    }

    result.textContent =
        "Lost frame and following frames retransmitted. This is a simplified Go-Back-N demonstration.";

    gbnFailed = false;
    gbnCompleted = true;
});

document.getElementById("gbnResetBtn").addEventListener("click", resetGBN);


// 6. WIRESHARK SAMPLE PACKET CAPTURE
const samplePackets = [
    ["192.168.1.1", "192.168.1.10", "TCP", "1200 bytes", "Success"],
    ["192.168.1.20", "192.168.1.10", "UDP", "850 bytes", "Success"],
    ["192.168.1.25", "192.168.1.1", "ICMP", "64 bytes", "Success"],
    ["192.168.1.30", "192.168.1.10", "HTTP", "1450 bytes", "Success"],
    ["192.168.1.10", "192.168.1.20", "TCP", "980 bytes", "Success"]
];

let packetNumber = 0;

document.getElementById("captureBtn").addEventListener("click", () => {
    const table = document.getElementById("packetTable");
    const result = document.getElementById("packetResult");

    samplePackets.forEach(packet => {
        packetNumber++;

        const row = document.createElement("tr");
        const values = [packetNumber, ...packet];

        values.forEach(value => {
            const cell = document.createElement("td");
            cell.textContent = value;
            row.appendChild(cell);
        });

        table.appendChild(row);
    });

    result.textContent =
        "Sample capture displayed: " + packetNumber + " packets.";
});

document.getElementById("clearCaptureBtn").addEventListener("click", () => {
    document.getElementById("packetTable").innerHTML = "";
    packetNumber = 0;
    document.getElementById("packetResult").textContent =
        "Sample packet list cleared.";
});


// 7. NS2 PERFORMANCE SAMPLE SIMULATION
document.getElementById("ns2RunBtn").addEventListener("click", () => {
    const throughput = 86.4;
    const packetLoss = 4;
    const delivery = 96;
    const delay = 12;

    document.getElementById("throughput").textContent =
        throughput + " Mbps";
    document.getElementById("packetLoss").textContent =
        packetLoss + "%";
    document.getElementById("deliveryRatio").textContent =
        delivery + "%";
    document.getElementById("averageDelay").textContent =
        delay + " ms";

    const canvas = document.getElementById("performanceChart");
    const ctx = canvas.getContext("2d");

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    const values = [65, 75, 60, 85, 72, 90, 86];
    const labels = ["1", "2", "3", "4", "5", "6", "7"];

    ctx.strokeStyle = "#38bdf8";
    ctx.lineWidth = 3;
    ctx.beginPath();

    values.forEach((value, index) => {
        const x = 50 + index * 85;
        const y = 210 - value * 1.8;

        if (index === 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
    });

    ctx.stroke();

    values.forEach((value, index) => {
        const x = 50 + index * 85;
        const y = 210 - value * 1.8;

        ctx.fillStyle = "#4ade80";
        ctx.beginPath();
        ctx.arc(x, y, 5, 0, Math.PI * 2);
        ctx.fill();

        ctx.fillStyle = "#e2e8f0";
        ctx.font = "12px Arial";
        ctx.fillText(labels[index], x - 3, 235);
    });

    ctx.fillStyle = "#94a3b8";
    ctx.font = "13px Arial";
    ctx.fillText("Sample Network Throughput", 50, 25);

    document.getElementById("ns2Result").textContent =
        "Sample performance simulation completed successfully.";
});

console.log("SCNM Smart Campus Network System loaded successfully.");