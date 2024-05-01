package com.example.npuzzle

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import android.view.View


@SuppressLint("ViewConstructor")
class PuzzleBoardView(context: Context, val n: Int, private val gameCompleteListener: GameCompleteListener) : View(context) {
    private val paint = Paint()
    private var containerWidth: Int = 0
    private var size = 0
    private var numberOfMovesMade = 0
    private val mat = Array(n) { Array(n) { PuzzleBlock(context, 0, 0F, 0F, 0F) } }
    private var emptyBlockIndex = Point(n - 1, n - 1)


    fun getNumberOfMovesMade(): Int {
        return numberOfMovesMade
    }
    interface GameCompleteListener {
        fun onGameComplete(score: Int)
        abstract fun displayGameOverDialogWithServerResponse(points: Int, moves: String)
    }

    init {
        paint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        containerWidth = measuredWidth
        if (containerWidth > 0) {
            initGame()
        }
    }

    fun initGame() {
        emptyBlockIndex = Point(n - 1, n - 1)
        size = containerWidth / n
        numberOfMovesMade = 0
        var x = 0
        var y = 0
        var ID = 1
        for (i in 0 until n) {
            for (j in 0 until n) {
                mat[i][j] = PuzzleBlock(context, ID, x.toFloat(), y.toFloat(), size.toFloat())
                ID++
                ID %= n * n
                x += size
            }
            x = 0
            y += size
        }
        shuffleMat()
    }

    private fun shuffleMat() {
        val iteration = 100
        for (i in 0 until iteration) {
            val options = mutableListOf<Point>()
            if (emptyBlockIndex.x + 1 < n) options.add(Point(emptyBlockIndex.x + 1, emptyBlockIndex.y))
            if (emptyBlockIndex.x - 1 >= 0) options.add(Point(emptyBlockIndex.x - 1, emptyBlockIndex.y))
            if (emptyBlockIndex.y + 1 < n) options.add(Point(emptyBlockIndex.x, emptyBlockIndex.y + 1))
            if (emptyBlockIndex.y - 1 >= 0) options.add(Point(emptyBlockIndex.x, emptyBlockIndex.y - 1))
            options.shuffle()
            val selectedIndex = options[0]
            swapBlock(selectedIndex.x, selectedIndex.y)
        }
    }

    private fun swapBlock(i: Int, j: Int) {
        val ID = mat[i][j].ID
        mat[i][j].ID = 0
        mat[emptyBlockIndex.x][emptyBlockIndex.y].ID = ID
        emptyBlockIndex = Point(i, j)
    }

    private fun makeMove(i: Int, j: Int) {
        swapBlock(i, j)
        numberOfMovesMade++
        invalidate()
        if (isSolution()) {
            gameCompleteListener.onGameComplete(calculateScore())
        }
    }

    private fun calculateScore(): Int {
        return 1000 - numberOfMovesMade
    }

    private fun isSolution(): Boolean {
        var count = 1
        for (i in 0 until n) {
            for (j in 0 until n) {
                if (mat[i][j].ID != count && count != n * n) {
                    return false
                }
                count++
            }
        }
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP && size > 0) {
            val i = (event.y / size).toInt()
            val j = (event.x / size).toInt()

            if (isValidMove(i, j)) {
                makeMove(i, j)
            }

            Log.d("PuzzleBoardView", "Touch at: ${event.x}:${event.y} - Moved to $i,$j")
        }
        return true
    }

    private fun isValidMove(i: Int, j: Int): Boolean {
        return (i + 1 == emptyBlockIndex.x && j == emptyBlockIndex.y) ||
            (i - 1 == emptyBlockIndex.x && j == emptyBlockIndex.y) ||
            (j + 1 == emptyBlockIndex.y && i == emptyBlockIndex.x) ||
            (j - 1 == emptyBlockIndex.y && i == emptyBlockIndex.x)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until n) {
            for (j in 0 until n) {
                mat[i][j].onDraw(canvas, paint)
            }
        }
    }
}
