package com.example.npuzzle

import MainFragment
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

class MainActivity : AppCompatActivity() {

    private val logTag = "MainActivity"
    private lateinit var socket: Socket
    val fragmentManager: FragmentManager = supportFragmentManager
    var n = 3 // size of the puzzle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(logTag, "onCreate")

        // Initialize Socket.IO connection
        initSocket()

        showMainFragment()
    }

    private fun initSocket() {
        try {
            // Update the URL to point to your server
            socket = IO.socket("http://10.0.2.2:3000")
            socket.connect()

            socket.on(Socket.EVENT_CONNECT) {
                Log.d(logTag, "Connected to server")
                runOnUiThread {
                    Toast.makeText(this, "Connected to server", Toast.LENGTH_SHORT).show()
                }
            }

            socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
                if (args.isNotEmpty()) {
                    val error = args[0] as Exception
                    Log.e(logTag, "Failed to connect to server", error)
                    runOnUiThread {
                        Toast.makeText(this, "Failed to connect: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e(logTag, "Failed to connect to server: No error details")
                }
            }

            socket.on(Socket.EVENT_DISCONNECT) {
                Log.d(logTag, "Disconnected from server")
                runOnUiThread {
                    Toast.makeText(this, "Disconnected from server", Toast.LENGTH_SHORT).show()
                }
            }

            // Setup socket events
            setupSocketEvents()
        } catch (e: URISyntaxException) {
            Log.e(logTag, "Error initializing socket", e)
        }
    }
    private fun setupSocketEvents() {
        socket.on(Socket.EVENT_CONNECT) {
            Log.d(logTag, "Connected to server")
            runOnUiThread {
                Toast.makeText(this, "Connected to server", Toast.LENGTH_SHORT).show()
            }
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Log.d(logTag, "Disconnected from server")
            runOnUiThread {
                Toast.makeText(this, "Disconnected from server", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showMainFragment() {
        val transaction = fragmentManager.beginTransaction()
        val fragment = MainFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.commit()
    }

    fun showPuzzleFragment(newN: Int) {
        n = newN
        val transaction = fragmentManager.beginTransaction()
        val fragment = PuzzleFragment()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
    }
}
