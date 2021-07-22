package com.punch.punchpower

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.punch.punchpower.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var resultBinding: ActivityResultBinding


    val power by lazy {
        intent.getDoubleExtra("power", 0.0) * 100
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultBinding = ActivityResultBinding.inflate(layoutInflater)
        val view = resultBinding.root
        val scoreLabel = resultBinding.scoreLabel
        val restartButton = resultBinding.restartButton
        setContentView(view)
        title = "펀치력결과"

        // 점수를 표시하는 TextView 에 점수를 표시
        scoreLabel.text = "${String.format("%.0f", power)} 점"

        restartButton.setOnClickListener {
            finish()
        }





    }
}