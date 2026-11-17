# ======================================================================
# SMART CAMPUS NETWORK SIMULATION - NS2 TCL SCRIPT
# B.Tech Computer Networks and Internet Protocols (CNIP) Laboratory
# Topology: 5 Campus Departments connected through Core Network
# ======================================================================

set ns [new Simulator]

# Open trace file and NAM animation file
set tracefile [open campus_simulation.tr w]
$ns trace-all $tracefile

set namfile [open campus_simulation.nam w]
$ns namtrace-all $namfile

# Define Finish Procedure
proc finish {} {
    global ns tracefile namfile
    $ns flush-trace
    close $tracefile
    close $namfile
    puts "Simulation finished. Output saved to campus_simulation.tr and campus_simulation.nam"
    exit 0
}

# Create Nodes
set n_cs      [$ns node] ;# Node 0: Computer Science Dept
set n_ece     [$ns node] ;# Node 1: ECE Dept
set n_lib     [$ns node] ;# Node 2: Library
set n_admin   [$ns node] ;# Node 3: Admin Office
set n_server  [$ns node] ;# Node 4: Server Room
set n_core_sw [$ns node] ;# Node 5: Campus Core Switch
set n_router  [$ns node] ;# Node 6: Campus Core Router

# Create Network Duplex Links
# Syntax: $ns duplex-link node1 node2 bandwidth delay queue_type
$ns duplex-link $n_cs      $n_core_sw  100Mb  3ms DropTail
$ns duplex-link $n_ece     $n_core_sw  100Mb  4ms DropTail
$ns duplex-link $n_lib     $n_core_sw  100Mb  3ms DropTail
$ns duplex-link $n_admin   $n_core_sw  100Mb  2ms DropTail
$ns duplex-link $n_server  $n_core_sw  1000Mb 1ms DropTail
$ns duplex-link $n_core_sw $n_router   1000Mb 2ms DropTail

# Set Queue Limits
$ns queue-limit $n_core_sw $n_server 50

# Setup UDP and CBR traffic from CS to Server Room
set udp0 [new Agent/UDP]
$ns attach-agent $n_cs $udp0
set null0 [new Agent/Null]
$ns attach-agent $n_server $null0
$ns connect $udp0 $null0

set cbr0 [new Application/Traffic/CBR]
$cbr0 set packetSize_ 1000
$cbr0 set interval_ 0.005
$cbr0 attach-agent $udp0

# Setup TCP Traffic from ECE to Server Room
set tcp0 [new Agent/TCP]
$ns attach-agent $n_ece $tcp0
set sink0 [new Agent/TCPSink]
$ns attach-agent $n_server $sink0
$ns connect $tcp0 $sink0

set ftp0 [new Application/FTP]
$ftp0 attach-agent $tcp0

# Schedule Events
$ns at 0.5 "$cbr0 start"
$ns at 1.0 "$ftp0 start"
$ns at 4.5 "$cbr0 stop"
$ns at 4.8 "$ftp0 stop"
$ns at 5.0 "finish"

# Run Simulation
$ns run
