package com.example.npuzzle

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class SocketManager {
    lateinit var socket: Socket
    // Make userId == socket.id

    public fun getUserId(): String {
        return socket.id()
    }

    fun initSocket() {
        try {
            socket = IO.socket("https://puzzle-game-api-wandering-voice-1090.fly.dev")
            socket.connect()
            setupSocketEvents()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun setupSocketEvents() {
        socket.on(Socket.EVENT_CONNECT) {
            println("Connected to server")
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            println("Disconnected from server")
        }
    }

    fun disconnect() {
        if (this::socket.isInitialized) {
            socket.disconnect()
        }
    }
}