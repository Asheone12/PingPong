package com.muen.pingpong

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class PingPongView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {
    private var ballX = 100f
    private var ballY = 1000f
    private var ballRadius = 20f
    private var ballSpeedX = 10f
    private var ballSpeedY = 10f

    private var paddleX = 0f
    private var paddleWidth = 300f
    private var paddleHeight = 40f

    private var score = 0
    private var gameOver = false

    private val paint = Paint()

    private val blocks = mutableListOf<RectF>()
    private val blockWidth = 100f
    private val blockHeight = 40f
    private val blockCount = 10
    private val blockPadding = 5f // 方块之间的间距
    private val rows = 10 // 顶部行数

    // 在onSizeChanged方法中初始化方块
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        while (blocks.size < blockCount) {
            var overlap = false
            val x = Random.nextFloat() * (width - blockWidth)
            val y = Random.nextFloat() * (blockHeight * rows) // 在顶部10行内随机生成
            val newBlock = RectF(x, y, x + blockWidth, y + blockHeight)

            // 检查新方块是否与现有方块重叠
            for (block in blocks) {
                if (RectF.intersects(block, newBlock)) {
                    overlap = true
                    break
                }
            }

            // 如果没有重叠，添加新方块
            if (!overlap) {
                blocks.add(newBlock)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画球
        paint.color = android.graphics.Color.RED
        canvas.drawCircle(ballX, ballY, ballRadius, paint)

        // 画挡板
        paint.color = android.graphics.Color.BLUE
        canvas.drawRect(paddleX, height - paddleHeight, paddleX + paddleWidth, height.toFloat(), paint)

        if (!gameOver) {
            // 更新球的位置
            ballX += ballSpeedX
            ballY += ballSpeedY

            // 绘制方块以及边框
            paint.color = android.graphics.Color.GREEN
            for (block in blocks) {
                canvas.drawRect(block, paint)
            }
            paint.color = android.graphics.Color.RED // 设置边框颜色
            paint.style = Paint.Style.STROKE // 设置画笔样式为描边
            paint.strokeWidth = 4f // 设置边框宽度
            for (block in blocks) {
                canvas.drawRect(block.left, block.top, block.right, block.bottom, paint)
            }
            paint.style = Paint.Style.FILL // 将画笔样式重置为填充


            // 检查球是否触碰到方块
            val iterator = blocks.iterator()
            while (iterator.hasNext()) {
                val block = iterator.next()
                if (ballX + ballRadius > block.left && ballX - ballRadius < block.right && ballY - ballRadius < block.bottom) {
                    iterator.remove() // 移除方块
                    ballSpeedY = -ballSpeedY // 改变球的移动方向
                    score++ // 加分
                }
            }

            // 检查球是否触碰到左边或右边的边缘
            if (ballX < ballRadius || ballX > width - ballRadius) {
                ballSpeedX = -ballSpeedX
            }
            // 检查球是否触碰到顶部或挡板
            if (ballY < ballRadius) {
                ballSpeedY = -ballSpeedY
                //score++  // 球触碰到顶部，分数增加
            } else if (ballY > height - paddleHeight - ballRadius && ballX > paddleX && ballX < paddleX + paddleWidth) {
                ballSpeedY = -ballSpeedY
            } else if (ballY > height - ballRadius) {
                // 球触碰到屏幕底部，游戏结束
                gameOver = true
            }

            // 检查是否所有方块都被消除
            if (blocks.isEmpty()) {
                gameOver = true
            }

            // 绘制分数
            paint.color = android.graphics.Color.GREEN
            paint.textSize = 60f
            canvas.drawText("Score: $score", 50f, 100f, paint)

            // 重新绘制画布
            invalidate()
        } else {
            // 如果游戏结束且blocks不为空，显示失败信息和最终分数
            if (blocks.isNotEmpty()) {
                paint.color = android.graphics.Color.BLACK
                paint.textSize = 80f
                canvas.drawText("Game Over", width / 2f - 200f, height / 2f, paint)
                canvas.drawText("Final Score: $score", width / 2f - 200f, height / 2f + 100f, paint)
            }else{
                // 绘制胜利信息
                paint.color = android.graphics.Color.BLACK
                paint.textSize = 80f
                canvas.drawText("You Win!", width / 2f - 200f, height / 2f, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                // Update the position of the paddle
                paddleX = event.x - paddleWidth / 2
                invalidate()
            }
        }
        return true
    }
}