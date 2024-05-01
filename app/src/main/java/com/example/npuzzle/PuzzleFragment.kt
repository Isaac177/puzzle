package com.example.npuzzle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import org.json.JSONException
import org.json.JSONObject

class PuzzleFragment : Fragment(), PuzzleBoardView.GameCompleteListener {
    private val logTag = "PuzzleFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(logTag, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_puzzle, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mainActivity: MainActivity = activity as MainActivity

        val puzzleBoardView = PuzzleBoardView(requireContext(), mainActivity.n, this)
        view?.findViewById<ViewGroup>(R.id.puzzle_container)?.addView(puzzleBoardView)

        view?.findViewById<Button>(R.id.button_new_game)?.setOnClickListener {
            puzzleBoardView.initGame()
            puzzleBoardView.invalidate()
        }

        setupSocketListeners(mainActivity)
    }

    private fun setupSocketListeners(mainActivity: MainActivity) {
        mainActivity.socketManager.socket.on("update_leaderboard") { args ->
            activity?.runOnUiThread {
                if (args.isNotEmpty()) {
                    try {
                        val response = JSONObject(args[0].toString())
                        val scoreId = response.getInt("id")
                        val moves = response.getString("numberOfMovesMade")
                        val createdAt = response.getString("createdAt")
                        val points = response.getInt("points")

                        displayGameOverDialogWithServerResponse(points, moves, mainActivity.socketManager.getUserId())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        // Handle JSON parsing errors or display an error message
                    }
                }
            }
        }
    }

    override fun onGameComplete(score: Int) {
        Log.d(logTag, "Game completed with score: $score")
        gameOver(score)
    }

    fun gameOver(points: Int) {
        val mainActivity: MainActivity = activity as MainActivity
        val userId = mainActivity.socketManager.getUserId()

        mainActivity.socketManager.socket.emit("submit_score", JSONObject().apply {
            put("userId", userId)
            put("points", points)
            put("puzzleSize", mainActivity.n)
        })
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
