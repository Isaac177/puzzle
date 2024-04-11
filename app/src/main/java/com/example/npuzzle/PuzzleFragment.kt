package com.example.npuzzle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class PuzzleFragment : Fragment() {

    private val logTag = "PuzzleFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(logTag, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_puzzle, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mainActivity: MainActivity = activity as MainActivity

        val puzzleBoardView = PuzzleBoardView(requireContext(), mainActivity.n)
        view?.findViewById<ViewGroup>(R.id.puzzle_container)?.addView(puzzleBoardView)

        view?.findViewById<Button>(R.id.button_new_game)?.setOnClickListener {
            puzzleBoardView.initGame()
            puzzleBoardView.invalidate()
        }
    }
}
