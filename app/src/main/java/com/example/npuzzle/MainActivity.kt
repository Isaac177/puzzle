package com.example.npuzzle

import MainFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import io.socket.client.Socket


class MainActivity : AppCompatActivity() {
    lateinit var socketManager: SocketManager
    private val logTag = "MainActivity"
    private lateinit var socket: Socket
    val fragmentManager: FragmentManager = supportFragmentManager
    var n = 3 // size of the puzzle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        socketManager = SocketManager()
        socketManager.initSocket()
        showMainFragment()
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
