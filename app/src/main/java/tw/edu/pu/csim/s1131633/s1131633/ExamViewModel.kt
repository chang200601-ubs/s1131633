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

    // 碰撞訊息
    private val _collisionMessage = MutableStateFlow("")
    val collisionMessage: StateFlow<String> = _collisionMessage.asStateFlow()

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
            Random.nextFloat() * (_screenWidth.value - 100)
        } else {
            0f
        }
        _serviceX.value = randomX

        // 重置到螢幕上方
        _serviceY.value = 0f

        // 清除碰撞訊息
        _collisionMessage.value = ""
    }

    fun checkCollision() {
        val serviceSize = 100f
        val roleSize = 300f
        val halfScreen = _screenHeight.value / 2f

        // 檢查是否碰撞到四個角色
        // role0 (嬰幼兒) - 左上角，下方切齊螢幕高度 1/2
        if (_serviceX.value < roleSize &&
            _serviceY.value + serviceSize >= halfScreen - roleSize &&
            _serviceY.value < halfScreen) {
            _collisionMessage.value = "（碰撞嬰幼兒圖示）"
            return
        }

        // role1 (兒童) - 右上角，下方切齊螢幕高度 1/2
        if (_serviceX.value + serviceSize > _screenWidth.value - roleSize &&
            _serviceY.value + serviceSize >= halfScreen - roleSize &&
            _serviceY.value < halfScreen) {
            _collisionMessage.value = "（碰撞兒童圖示）"
            return
        }

        // role2 (成人) - 左下角
        if (_serviceX.value < roleSize &&
            _serviceY.value + serviceSize >= _screenHeight.value - roleSize) {
            _collisionMessage.value = "（碰撞成人圖示）"
            return
        }

        // role3 (一般民眾) - 右下角
        if (_serviceX.value + serviceSize > _screenWidth.value - roleSize &&
            _serviceY.value + serviceSize >= _screenHeight.value - roleSize) {
            _collisionMessage.value = "（碰撞一般民眾圖示）"
            return
        }

        // 檢查是否掉到最下方（沒碰到任何角色）
        if (_serviceY.value >= _screenHeight.value - 100) {
            _collisionMessage.value = "（掉到最下方）"
        }
    }
}