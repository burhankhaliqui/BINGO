<h1 align="center">🎲 JavaFX Bingo</h1>
<h3 align="center">A modular Java 17 desktop Bingo game — JavaFX GUI, console fallback, and LAN multiplayer over raw TCP sockets</h3>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17"/>
  <img src="https://img.shields.io/badge/JavaFX-17-blue?style=for-the-badge&logo=java&logoColor=white" alt="JavaFX 17"/>
  <img src="https://img.shields.io/badge/Maven-Wrapper-red?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/Modular-module--info.java-purple?style=for-the-badge&logo=java&logoColor=white" alt="Modular"/>
  <img src="https://img.shields.io/badge/Multiplayer-TCP%20Sockets-green?style=for-the-badge&logo=socketdotio&logoColor=white" alt="TCP Sockets"/>
</p>

<p align="center">
  Made with ❤️ by <b>Burhan</b>
</p>

<p align="center">
  <img align="center" width="500" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="java" />
</p>

---

## 📌 Overview

**JavaFX Bingo** is a modular Java 17 desktop game built around a classic Bingo-style card-marking loop. It ships with **two entry points**:

- 🎨 A full **JavaFX GUI** (`BingoApp`) — splash screen, animated menus, FXML-driven game boards.
- 🖥️ A **console fallback** (`Main`) — same gameplay, no graphics required.

The project combines FXML screen composition, media playback, controller-based navigation, and a small game model that supports both **single-player** and **LAN multiplayer** flows over plain TCP sockets.

> 🔭 Current GUI entry point: `mediaworking.GameSmiths.BingoApp`
> 🔭 Console entry point: `mediaworking.GameSmiths.Main`

---

## ✨ What This Project Does

- 🎬 JavaFX splash screen with animated menu flow
- 🧭 Menu navigation for play modes, how-to-play content, and exit handling
- 👤 Single-player and 👥 multiplayer game paths
- 🎫 Board generation and number-marking logic
- 🤖 Bot and 🙋 human player abstractions
- 🎵 Media assets — background music, click sounds, splash video, visual presentation
- 🧩 FXML-based screen composition for the GUI

---

## 🛠️ Tech Stack

<p align="left">
<a href="https://www.java.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="java" width="40" height="40"/> </a>
<a href="https://openjfx.io/" target="_blank" rel="noreferrer"> <img src="https://upload.wikimedia.org/wikipedia/commons/8/8e/JavaFX_Logo.svg" alt="javafx" width="40" height="40"/> </a>
<a href="https://maven.apache.org/" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/maven/maven-original.svg" alt="maven" width="40" height="40"/> </a>
<a href="https://www.linux.org/" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/linux/linux-original.svg" alt="linux" width="40" height="40"/> </a>
<a href="https://www.microsoft.com/en-us/windows" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/windows8/windows8-original.svg" alt="windows" width="40" height="40"/> </a>
</p>

| Layer | Technology |
|---|---|
| Language | Java 17 |
| GUI Framework | JavaFX 17 |
| Build Tool | Maven Wrapper (`mvnw` / `mvnw.cmd`) |
| Architecture | Modular Java (`module-info.java`) |
| Networking | Raw TCP `Socket` / `ServerSocket` |
| UI Definition | FXML + Controllers |

---

## 📂 Repository Structure

```text
BINGO/
  mvnw
  mvnw.cmd
  pom.xml
  src/
    main/
      java/
        module-info.java
        mediaworking/GameSmiths/
          BingoApp.java
          BotPlayer.java
          HumanPlayer.java
          Main.java
          MediaPlayerController.java
          Menu.java
          Player.java
          SinglePlayerBoardController.java
          Winner.java
          controllers/
            ClientController.java
            GameModeController.java
            HowToPlayController.java
            MenuController.java
            MultiplayerBoardController.java
            MultiplayerSetupController.java
            MultiplayerWinnerController.java
            OnlineGameBoardController.java
            ServerController.java
            SetupController.java
            SinglePlayerSetupController.java
            SinglePlayerWinnerController.java
            WinnerScreenController.java
          Model/
            Board.java
      resources/
        mediaworking/GameSmiths/
          12343.mp3
          button-click.mp3
          button-clickk.mp3
          splash video and image assets
          FXML views for menu, setup, boards, winners, and help screens
```

---

## 🔁 Application Flow

### 🎨 GUI Flow

1. `BingoApp` starts the JavaFX application.
2. A splash media screen is loaded from `Media-Player.fxml`.
3. The main menu is loaded from `menu.fxml`.
4. Menu actions route into game mode, how-to-play, and exit paths.
5. Additional screens are loaded through dedicated FXML controllers.

### 🖥️ Console Flow

1. `Main` launches a text-based menu loop.
2. The player selects play, settings, how-to-play, or exit.
3. Single-player mode and multiplayer mode are handled through the console game logic.

---

## ✅ Prerequisites

- ☕ Java Development Kit **17**
- 📦 Maven, or the included Maven Wrapper
- 🖥️ Windows, macOS, or Linux with JavaFX-compatible runtime support

---

## 🚀 How to Run

### ▶️ Recommended: Run the GUI

From the project root:

```bash
./mvnw clean compile
```

Then run the JavaFX application entry point:

```bash
java --module-path "target/classes;<your-maven-dependency-classpath>" --module mediaworking.javafxfinalsetupburhan/mediaworking.GameSmiths.BingoApp
```

On Windows PowerShell, the dependency classpath can be generated with:

```powershell
./mvnw dependency:build-classpath "-Dmdep.outputFile=cp.txt"
```

Then launch with:

```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
java --module-path "target/classes;$(Get-Content -Raw cp.txt)" --module mediaworking.javafxfinalsetupburhan/mediaworking.GameSmiths.BingoApp
```

### 🖥️ Console Fallback

If you want the terminal version instead, launch the console entry point directly after compiling:

```powershell
java --module-path "target/classes;$(Get-Content -Raw cp.txt)" --module mediaworking.javafxfinalsetupburhan/mediaworking.GameSmiths.Main
```

You can also run the `Main` class from your IDE if you prefer a debugger-friendly workflow.

---

## 📦 Notes on JavaFX and Packaging

- The project uses a modular layout, so the module name declared in `module-info.java` matters when launching from the command line.
- JavaFX media assets are loaded from the classpath under `src/main/resources/mediaworking/GameSmiths/`.
- If the JavaFX runtime version differs slightly from the FXML metadata version, the app may log a warning but still run.
- The repository currently keeps compiled output in `target/` ignored by Git, which is the correct behavior for source-only version control.

---

## 🎮 Features in Detail

### 🧭 Screens and Navigation

- Splash screen with media playback
- Main menu with play, how-to-play, and exit actions
- Game mode selection screen
- Single-player and multiplayer setup screens
- Winner/result screens for both play styles

### 🧠 Gameplay Model

- `Board` handles the Bingo card state
- `Player` defines shared player behavior
- `HumanPlayer` and `BotPlayer` specialize the player logic
- `Winner` evaluates end-game conditions

### 🌐 Multiplayer and Networking

- The project contains online multiplayer server/client controllers
- Socket-based communication is used in the console multiplayer path
- Board state is synchronized through turn-based number marking

---

## 🌐 Networking Architecture

This is where the project gets its stronger networking shape: the multiplayer runtime uses classic **TCP socket orchestration** rather than a lightweight local callback model. The architecture is intentionally straightforward, deterministic, and easy to reason about during play.

### 🖧 Server-Side Flow

The host path is implemented with a `ServerSocket` bound to port **`12345`**. The server waits in an accept loop, takes incoming client connections one at a time, and then wraps each socket with a `BufferedReader` and `PrintWriter` pair so the game can exchange line-based text messages. Each connected player is assigned a name, added to the player roster, and then folded into the shared turn sequence.

Operationally, the server does the heavy lifting:

- Listens on a fixed port and blocks on `accept()` until players connect
- Prompts each client for a player name over the socket stream
- Collects per-client input/output handles for later turn management
- Broadcasts lobby messages, board state, and turn prompts to every connected player
- Reads numeric turn input, validates it against the player's board, and rejects invalid selections
- Marks the chosen number across all boards so the game state remains synchronized
- Detects disconnects and attempts to cleanly close the server socket and client streams

### 💻 Client-Side Flow

The client path opens a `Socket` to the server address and port, then immediately wires the socket streams into a text protocol. The client listens for server messages with a blocking `readLine()` loop and responds with user input when prompted. In practice, this means the gameplay is message-driven: the server narrates the state, the client reflects it, and the player reacts through terminal input.

That client-side contract is simple but effective:

- Connect to the host using the configured IP address and port
- Maintain a `BufferedReader` for inbound server messages
- Maintain a `PrintWriter` for outbound player responses
- Wait for server prompts such as name entry, turn entry, and board updates
- Send numeric selections back to the host as plain text

### 📡 Protocol Style

The protocol is deliberately minimal and human-readable. Messages are plain text, one logical line at a time, which makes the game easy to trace in a terminal and easy to debug without a packet sniffer. That also means the flow is synchronous and conversational rather than event-framed or binary-encoded.

### 💪 Networking Strengths

- Clear separation between host authority and client participation
- Simple line-based messaging that is easy to inspect and maintain
- Deterministic turn handling because the server owns the authoritative game state
- Straightforward recovery path for disconnects and invalid input
- No external broker or dependency layer, so the transport stays direct and transparent

### ⚠️ Networking Caveats

- The port is fixed, so collisions are possible if another service already uses `12345`
- The current protocol is text-based rather than resilient to packet loss or partial-frame recovery
- Server logic is synchronous, so long blocking operations can stall the game loop if a client becomes unresponsive
- The implementation is good for a classroom-style LAN game, but it is not a production-grade distributed game backend

### 🎬 Media and Presentation

- Background audio is loaded on application startup
- UI click sounds are wired into menu actions
- A splash media file is used before the main menu appears
- Image assets are stored alongside the FXML views for easy packaging

---

## 💡 Development Tips

- Keep FXML file paths aligned with `FXMLLoader` resource lookups
- Keep media file names exact, including case and extensions
- When changing the module name, update the launch command and `module-info.java` together
- If you move controllers or model classes, update both package declarations and FXML `fx:controller` references
- For GUI debugging, start with `BingoApp` rather than the console `Main` class

---

## 🧰 Troubleshooting

<details>
<summary><b>☕ Java Is Not Found</b></summary>
<br>
If Maven reports that <code>JAVA_HOME</code> is missing or <code>java</code> is not found, install JDK 17 and set <code>JAVA_HOME</code> before running the app.
</details>

<details>
<summary><b>🪟 GUI Does Not Open</b></summary>
<br>
Check that the JavaFX runtime jars are on the module path and that <code>BingoApp</code> is the class being launched.
</details>

<details>
<summary><b>🎵 Missing Media or FXML</b></summary>
<br>
If a screen or audio asset fails to load, verify that the file exists under <code>src/main/resources/mediaworking/GameSmiths/</code> and that the resource path in code matches the file name exactly.
</details>

<details>
<summary><b>⚠️ JavaFX Version Warnings</b></summary>
<br>
Version mismatch warnings during FXML loading are usually informational, not fatal. The app can still start if the screens and dependencies resolve correctly.
</details>

---



<p align="center">
  Made with ☕ and JavaFX by <b>Burhan</b>
</p>
