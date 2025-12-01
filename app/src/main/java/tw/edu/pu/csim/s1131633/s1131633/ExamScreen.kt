package tw.edu.pu.csim.s1131633.s1131633

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ExamScreen(viewModel: ExamViewModel) {
    val screenWidth by viewModel.screenWidth.collectAsState()
    val screenHeight by viewModel.screenHeight.collectAsState()
    val serviceY by viewModel.serviceY.collectAsState()
    val serviceX by viewModel.serviceX.collectAsState()
    val currentService by viewModel.currentService.collectAsState()
    val collisionMessage by viewModel.collisionMessage.collectAsState()
    val score by viewModel.score.collectAsState() // <-- 取得分數狀態
    val density = LocalDensity.current

    // 控制是否正在拖曳
    var isDragging by remember { mutableStateOf(false) }
    var isAnimating by remember { mutableStateOf(false) }

    // 啟動掉落動畫
    LaunchedEffect(screenHeight, isDragging) {
        if (screenHeight > 0 && !isAnimating && !isDragging) {
            isAnimating = true
            viewModel.resetService()

            // 每 0.1 秒往下掉 20px
            while (serviceY < screenHeight - 100 && !isDragging && collisionMessage.isEmpty()) {
                delay(100)
                viewModel.updateServicePosition(serviceX, serviceY + 20f)
                viewModel.checkCollision()
            }

            // 如果掉到最下方或碰撞，稍作停頓後重新開始
            if (serviceY >= screenHeight - 100 || collisionMessage.isNotEmpty()) {
                // 再次檢查碰撞/掉落 (確保在 loop 結束時的最後狀態被處理)
                viewModel.checkCollision()

                // 模擬圖片中要求的 "暫停3秒" 再出下一題 (這裡使用 1 秒，可自行調整)
                delay(1000)

                // 檢查是否掉到最下方（如果已經在 checkCollision 處理，這裡不需要再次處理，但要確保動畫狀態重設）
                if (serviceY >= screenHeight - 100 && collisionMessage.isEmpty()) {
                    viewModel.checkCollision()
                    delay(1000)
                }

                viewModel.resetService()
                isAnimating = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)
            .onGloballyPositioned { coordinates ->
                viewModel.updateScreenSize(
                    coordinates.size.width,
                    coordinates.size.height
                )
            }
    ) {
        // 中間主要內容
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 圖片
            Image(
                painter = painterResource(id = R.drawable.happy),
                contentDescription = "瑪利亞",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 兩行主要文字
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "瑪利亞基金會服務大考驗",
                    color = Color.Black,
                    fontSize = 16.sp,
                )

                Text(
                    text = "作者：資管二B 張伊傑",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }

            // 螢幕大小和成績資訊
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "螢幕大小：$screenWidth × $screenHeight",
                color = Color.Black,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                // 顯示實際分數和碰撞訊息
                text = "成績：$score 分 $collisionMessage",
                color = Color.Black,
                fontSize = 14.sp
            )
        }

        // 掉落的服務圖示（可拖曳）
        if (screenHeight > 0) {
            val serviceDrawable = when (currentService) {
                0 -> R.drawable.service0
                1 -> R.drawable.service1
                2 -> R.drawable.service2
                else -> R.drawable.service3
            }

            Image(
                painter = painterResource(id = serviceDrawable),
                contentDescription = "服務圖示",
                modifier = Modifier
                    .size(with(density) { 300.toDp() })
                    .offset(
                        x = with(density) { serviceX.toDp() },
                        y = with(density) { serviceY.toDp() }
                    )
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                            },
                            onDragEnd = {
                                isDragging = false
                                isAnimating = false

                                // 檢查碰撞
                                viewModel.checkCollision()

                                // 拖曳結束後等待 1 秒 (可視為圖片要求的 3 秒) 後重新開始
                                kotlinx.coroutines.GlobalScope.launch {
                                    delay(1000)
                                    viewModel.resetService()
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val newX = (serviceX + dragAmount.x).coerceIn(
                                    0f,
                                    (screenWidth - 300).toFloat() // 修正邊界以匹配圖示大小
                                )
                                val newY = (serviceY + dragAmount.y).coerceIn(
                                    0f,
                                    (screenHeight - 300).toFloat() // 修正邊界以匹配圖示大小
                                )
                                viewModel.updateServicePosition(newX, newY)
                            }
                        )
                    },
                contentScale = ContentScale.Fit
            )
        }

        // 四個角色圖示 (保持原樣)
        if (screenHeight > 0) {
            val roleSizeDp = with(density) { 300.toDp() }
            // 角色圖示在畫面中央分隔線的邊緣
            val yOffsetTop = with(density) { (screenHeight / 2 - 300).toDp() }
            val yOffsetBottom = 0.dp // 置於底部

            // 左上角 - 嬰幼兒 (role0)
            Image(
                painter = painterResource(id = R.drawable.role0),
                contentDescription = "嬰幼兒",
                modifier = Modifier
                    .size(roleSizeDp)
                    .align(Alignment.TopStart)
                    .offset(y = yOffsetTop),
                contentScale = ContentScale.Fit
            )

            // 右上角 - 兒童 (role1)
            Image(
                painter = painterResource(id = R.drawable.role1),
                contentDescription = "兒童",
                modifier = Modifier
                    .size(roleSizeDp)
                    .align(Alignment.TopEnd)
                    .offset(y = yOffsetTop),
                contentScale = ContentScale.Fit
            )

            // 左下角 - 成人 (role2)
            Image(
                painter = painterResource(id = R.drawable.role2),
                contentDescription = "成人",
                modifier = Modifier
                    .size(roleSizeDp)
                    .align(Alignment.BottomStart)
                    .offset(y = yOffsetBottom),
                contentScale = ContentScale.Fit
            )

            // 右下角 - 一般民眾 (role3)
            Image(
                painter = painterResource(id = R.drawable.role3),
                contentDescription = "一般民眾",
                modifier = Modifier
                    .size(roleSizeDp)
                    .align(Alignment.BottomEnd)
                    .offset(y = yOffsetBottom),
                contentScale = ContentScale.Fit
            )
        }
    }
}