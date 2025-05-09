# JavaFX Client-Server Game

This project is a **client-server** application built with **JavaFX** that supports multiplayer functionality using **multithreading**. It allows multiple players to connect to the server simultaneously and interact within the game.

## Project Info

This project was developed as part of a **group assignment** for a university course. It demonstrates the use of **JavaFX** for building interactive UIs and **multithreading** for supporting real-time communication between multiple clients and the server.

## Features

- **Client-Server Architecture**: The server handles multiple clients (players) independently, allowing real-time interaction.
- **Multithreading**: Utilizes Java’s **multithreading** capabilities to ensure smooth, concurrent gameplay for all connected players.
- **JavaFX**: The user interface is built using JavaFX, providing a responsive and interactive GUI for the players.
- **Real-time Updates**: The server sends real-time updates to all clients, ensuring synchronization across multiple players.
- **Game Logic**: The server manages the game state and logic, while the client interacts with the user and sends updates to the server.

## Requirements

- Java 8 or higher
- JavaFX library

## How It Works

### Server

1. The **server** listens for incoming client connections on a specific port.
2. Each new client (player) gets assigned a **new thread** to handle their communication with the server.
3. The server manages the game state and broadcasts updates to all connected clients.

### Client

1. The **client** connects to the server using the provided IP and port.
2. The client’s GUI is created using **JavaFX**, where players can interact with the game.
3. Players' actions are sent to the server, and real-time updates are received to reflect changes in the game state.
