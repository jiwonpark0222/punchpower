package com.punch.punchpower

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import com.punch.punchpower.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    val TAG = "MainActivity"

    var maxPower = 0.0
    var isStart = false
    var startTime = 0L

    // Sensor 관리자 객체. lazy 로 실제 사용될때 초기화 한다.
    val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    // 센서 이벤트를 처리하는 리스너
    val eventListener: SensorEventListener = object: SensorEventListener{
        override fun onSensorChanged(event: SensorEvent?) {
            mainBinding = ActivityMainBinding.inflate(layoutInflater)
            val stateLabel = mainBinding.stateLabel

            event?.let {
                // 측정된 센서 값이 선형 가속도 타입이 아니면 바로 리턴
                if (event.sensor.type != Sensor.TYPE_LINEAR_ACCELERATION) return@let

                // 각 좌표값을 제곱하여 음수값을 없애고, 값의 차이를 극대화
                val power = Math.pow(event.values[0].toDouble(), 2.0) + Math.pow(event.values[1].toDouble(), 2.0) + Math.pow(event.values[2].toDouble(), 2.0)

                if (power > 20 && !isStart){
                    // 측정시작
                    Log.d(TAG,"측정시작")
                    startTime = System.currentTimeMillis()
                    isStart = true
                }

                // 측정이 시작된 경우
                if (isStart){
                    Log.d(TAG,"측정시작됫을때")
                    // 5초간 최대값을 측정, 현재 측정된 값이 지금까지 측정된 최대 값보다 크면 최대값을 현재 값으로 변경.
                    if (maxPower < power) maxPower = power

                    // 측정 중인 것을 사용자에게 알려줌
                    stateLabel.text = "펀치력을 측정하고 있습니다"

                    // 최초 측정후 3초가 지났으면 측정을 끝낸다.
                    if (System.currentTimeMillis() - startTime > 3000){
                        isStart = false
                        punchPowerTestComplete(maxPower)
                    }
                }

            }

        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        Log.d("MAINACTIVITY","여기서 바로만들어짐")
        val view = mainBinding.root
        val stateLabel = mainBinding.stateLabel
        val imageView = mainBinding.imageView

        setContentView(view)
//        imageView.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.tran))

    }

    override fun onStart() {
        super.onStart()
        initGame()
    }

    private fun initGame(){
        val stateLabel = mainBinding.stateLabel
        maxPower = 0.0
        isStart = false
        startTime = 0L
        stateLabel.text = "핸드폰을 손에 쥐고 주먹을 내지르세요"
        val imageView = mainBinding.imageView
        imageView.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.tran))
        //센서의 변화 값을 처리할 리스너를 등록한다.
        // TYPE_LINEAR_ACCELERATION 은 중력값을 제외하고 x, y, z 축에 측정된 가속도만 계산되어 나온다.
        sensorManager.registerListener(eventListener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL)


    }

    fun punchPowerTestComplete(power: Double){
        Log.d(TAG, "측정완료: power: ${String.format("%.5f", power)}")
        sensorManager.unregisterListener(eventListener)

        val intent = Intent(this@MainActivity, ResultActivity::class.java)
        intent.putExtra("power", power)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        try {
            sensorManager.unregisterListener(eventListener)
        }catch (e:Exception){}
    }
}