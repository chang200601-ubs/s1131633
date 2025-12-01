package tw.edu.pu.csim.s1131633.s1131633

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class ExamViewModel : ViewModel() {
    private val _screenWidth = MutableStateFlow(0)
    val screenWidth: StateFlow<Int> = _screenWidth.asStateFlow()

    private val _screenHeight = MutableStateFlow(0)
    val screenHeight: StateFlow<Int> = _screenHeight.asStateFlow()

    // 服務圖示的位置狀態
    private val _serviceY = MutableStateFlow(0f)
    val serviceY: StateFlow<Float> = _serviceY.asStateFlow()

    private val _serviceX = MutableStateFlow(0f)
    val serviceX: StateFlow<Float> = _serviceX.asStateFlow()

    // 當前顯示的服務圖示 (service0 ~ service3)
    private val _currentService = MutableStateFlow(0)
    val currentService: StateFlow<Int> = _currentService.asStateFlow()

    fun updateScreenSize(width: Int, height: Int) {
        _screenWidth.value = width
        _screenHeight.value = height
    }

    fun updateServicePosition(x: Float, y: Float) {
        _serviceX.value = x
        _serviceY.value = y
    }

    fun resetService() {
        // 隨機選擇一個服務圖示 (0-3)
        _currentService.value = Random.nextInt(0, 4)

        // 隨機 X 位置 (水平中間範圍)
        val randomX = if (_screenWidth.value > 0) {
            Random.nextFloat() * (_screenWidth.value - 300)
        } else {
            0f
        }
        _serviceX.value = randomX

        // 重置到螢幕上方
        _serviceY.value = 0f
    }
}