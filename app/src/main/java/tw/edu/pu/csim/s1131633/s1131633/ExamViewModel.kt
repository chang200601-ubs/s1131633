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

    // 成績
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    // 碰撞訊息 (用於底部的Toast顯示)
    private val _collisionMessage = MutableStateFlow("")
    val collisionMessage: StateFlow<String> = _collisionMessage.asStateFlow()

    // 服務名稱 (對應 service0, service1, service2, service3) - 根據圖片修改
    private val serviceNames = arrayOf("極早期療育", "聽語服務", "極重多障", "輔具服務")

    // 角色名稱 (對應 role0, role1, role2, role3)
    private val roleNames = arrayOf("嬰幼兒", "兒童", "成人", "一般民眾")

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
            // 讓圖示在水平中央範圍內出現
            Random.nextFloat() * (_screenWidth.value - 300)
        } else {
            0f
        }
        _serviceX.value = randomX

        // 重置到螢幕上方
        _serviceY.value = 0f

        // 清除碰撞訊息
        _collisionMessage.value = ""
    }

    // 取得該服務圖示正確對應的角色名稱
    private fun getCorrectRoleName(serviceIndex: Int): String {
        return when (serviceIndex) {
            0 -> roleNames[0] // 極早期療育 -> 嬰幼兒
            1 -> roleNames[1] // 聽語服務 -> 兒童
            2 -> roleNames[2] // 極重多障 -> 成人
            3 -> roleNames[3] // 輔具服務 -> 一般民眾
            else -> "未知"
        }
    }

    fun checkCollision() {
        val serviceSize = 300f // 圖示大小 (與 ExamScreen 保持一致)
        val roleSize = 300f
        val halfScreen = _screenHeight.value / 2f

        // 檢查是否碰撞到四個角色
        // role0 (嬰幼兒) - 左上角
        if (_serviceX.value < roleSize &&
            _serviceY.value + serviceSize >= halfScreen - roleSize &&
            _serviceY.value < halfScreen) {
            handleCollision(0, roleNames[0])
            return
        }

        // role1 (兒童) - 右上角
        if (_serviceX.value + serviceSize > _screenWidth.value - roleSize &&
            _serviceY.value + serviceSize >= halfScreen - roleSize &&
            _serviceY.value < halfScreen) {
            handleCollision(1, roleNames[1])
            return
        }

        // role2 (成人) - 左下角
        if (_serviceX.value < roleSize &&
            _serviceY.value + serviceSize >= _screenHeight.value - roleSize) {
            handleCollision(2, roleNames[2])
            return
        }

        // role3 (一般民眾) - 右下角
        if (_serviceX.value + serviceSize > _screenWidth.value - roleSize &&
            _serviceY.value + serviceSize >= _screenHeight.value - roleSize) {
            handleCollision(3, roleNames[3])
            return
        }

        // 檢查是否掉到最下方（沒碰到任何角色）
        if (_serviceY.value >= _screenHeight.value - 300) { // 邊界修正
            // 掉落到最下方，執行減分邏輯
            _score.value -= 1
            if (_score.value < 0) {
                _score.value = 0
            }

            val currentServiceName = serviceNames[_currentService.value]
            val correctRoleName = getCorrectRoleName(_currentService.value)

            // 設置訊息：符合圖片格式
            _collisionMessage.value = "$currentServiceName，屬於 ${correctRoleName} 方面的服務"
        }
    }

    private fun handleCollision(roleIndex: Int, roleName: String) {
        val currentServiceIndex = _currentService.value
        val currentServiceName = serviceNames[currentServiceIndex]
        val correctRoleName = getCorrectRoleName(currentServiceIndex)

        // 判斷是否配對正確
        val isCorrect = when (currentServiceIndex) {
            0 -> roleIndex == 0  // 極早期療育 -> 嬰幼兒
            1 -> roleIndex == 1  // 聽語服務 -> 兒童
            2 -> roleIndex == 2  // 極重多障 -> 成人
            3 -> roleIndex == 3  // 輔具服務 -> 一般民眾
            else -> false
        }

        if (isCorrect) {
            _score.value += 1
        } else {
            // 碰撞錯誤，減分
            _score.value -= 1
            if (_score.value < 0) {
                _score.value = 0
            }
        }

        // 設置訊息：符合圖片格式
        _collisionMessage.value = "$currentServiceName，屬於 ${correctRoleName} 方面的服務"
    }

    fun getScore(): Int = _score.value
}