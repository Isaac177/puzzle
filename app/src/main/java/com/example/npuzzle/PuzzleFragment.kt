package com.example.npuzzle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import org.json.JSONObject

class PuzzleFragment : Fragment(), PuzzleBoardView.GameCompleteListener {

    private val logTag = "PuzzleFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(logTag, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_puzzle, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mainActivity: MainActivity = activity as MainActivity

        // Pass `this` as the GameCompleteListener
        val puzzleBoardView = PuzzleBoardView(requireContext(), mainActivity.n, this)
        view?.findViewById<ViewGroup>(R.id.puzzle_container)?.addView(puzzleBoardView)

        view?.findViewById<Button>(R.id.button_new_game)?.setOnClickListener {
            puzzleBoardView.initGame()
            puzzleBoardView.invalidate()
        }
    }

    override fun onGameComplete(score: Int) {
        Log.d(logTag, "Game completed with score: $score")
        // Assuming gameOver is called to handle the game over logic
        gameOver(score)
    }

    override fun displayGameOverDialogWithServerResponse(points: Int, moves: String) {
        TODO("Not yet implemented")
    }

    fun gameOver(points: Int) {
        val mainActivity: MainActivity = activity as MainActivity
        val userId = mainActivity.socketManager.getUserId()

        mainActivity.socketManager.socket.emit("submit_score", JSONObject().apply {
            put("userId", userId)
            put("points", points)
            put("puzzleSize", mainActivity.n)
        })

        mainActivity.socketManager.socket.on("update_leaderboard") { args ->
            if (args.isNotEmpty()) {
                val response = JSONObject(args[0].toString())
                val scoreId = response.getInt("id")
                val moves = response.getString("moves")
                val createdAt = response.getString("createdAt")

                activity?.runOnUiThread {
                    displayGameOverDialogWithServerResponse(points, moves, userId)
                }
            }
        }
    }

    private fun displayGameOverDialogWithServerResponse(points: Int, moves: String, userId: String) {
        val message = "Game Over!\nScore: $points\nUser ID: $userId"
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Game Completed")
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }
}
