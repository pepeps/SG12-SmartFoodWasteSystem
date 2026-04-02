# Smart Retail Distributed System (gRPC + JmDNS)

## Overview

This project is a distributed system designed for a retail store (e.g., Tesco) to monitor inventory, predict demand, and reduce food waste.

The system is built using **gRPC** for communication and **JmDNS** for service discovery. A **Java Swing GUI** is used to orchestrate and visualise the system.

---

## System Architecture

The system is composed of three independent services:

### 1. Inventory Service

* Provides stock information
* Sends expiry alerts (server streaming)

### 2. Demand Service

* Receives sales data (client streaming)
* Predicts future demand (unary)

### 3. Recommendation Service

* Generates waste reduction suggestions (bidirectional streaming)
* Produces summary reports (unary)

---

## Technologies Used

* Java (JDK 17+ recommended)
* gRPC (Java)
* Protocol Buffers (proto3)
* JmDNS (Service Discovery)
* Java Swing (GUI)
* Maven / NetBeans

---

## Project Structure

```
src/
 ├── common/
 │    ├── jmdns/              # Service discovery
 │    └── logging/            # LogUtil class
 │
 ├── services/
 │    ├── inventory/
 │    ├── demand/
 │    └── recommendation/
 │
 ├── generated/               # gRPC generated files
 └── gui/                     # Swing GUI (Orchestrator)
```

---

## How to Run

### Option 1 – Using GUI (Recommended)

Run:

```
MainGUI
```

From the GUI you can:

* Start all **servers automatically**
* Run individual **clients**
* Execute the full system flow
* View logs in real time

👉 This is the easiest way to run the system.

---

### Option 2 – Manual Execution

#### Step 1 – Start Servers

```
InventoryServer
DemandServer
RecommendationServer
```

Each service:

* Starts a gRPC server
* Registers itself using JmDNS

#### Step 2 – Run Clients

```
InventoryClient
DemandClient
RecommendationClient
```

---

## gRPC Communication Types

| Service        | RPC Type                        |
| -------------- | ------------------------------- |
| Inventory      | Unary + Server Streaming        |
| Demand         | Client Streaming + Unary        |
| Recommendation | Bidirectional Streaming + Unary |

---

## Service Discovery (JmDNS)

Each service registers itself using:

```
_service._tcp.local.
```

Clients dynamically discover services using:

```java
JmDNSServiceDiscovery.discoverService(...)
```

---

## Logging

The system uses a custom `LogUtil` class:

* Logs appear in:

  * Console
  * GUI
* Levels:

  * INFO
  * WARN
  * ERROR

---

## Example Scenario

Imagine a Tesco store:

1. Inventory Service detects products close to expiry
2. Demand Service predicts low demand
3. Recommendation Service suggests:

   * Discounts
   * Promotions

This helps reduce food waste and improve efficiency.

---

## Known Limitations

* No authentication/security implemented
* JmDNS may require local network permissions (especially on macOS)
* Services must be running before clients (handled automatically by GUI)

---

## Future Improvements

* Add authentication (gRPC metadata)
* Add persistence (database)
* Improve UI interactivity
* Add retries and deadlines

---

## Author

Jose Perez Santamaria
HDip in Science in Computing – Distributed Systems CA
