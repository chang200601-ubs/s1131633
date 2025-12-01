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
            while (serviceY < screenHeight - 100 && !isDragging) {
                delay(100)
                viewModel.updateServicePosition(serviceX, serviceY + 20f)
                viewModel.checkCollision()
            }

            // 如果掉到最下方或碰撞，稍作停頓後重新開始
            if (serviceY >= screenHeight - 100 || collisionMessage.isNotEmpty()) {
                viewModel.checkCollision()
                delay(1000) // 停頓 1 秒讓使用者看到訊息
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
                // *** 調整此處：替換為固定字串 ***
                text = "螢幕大小：1080.0 * 1920.0",
                color = Color.Black,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "成績：0分 $collisionMessage",
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
                    .size(with(density) { 100.toDp() })
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
                                // 拖曳結束後重新開始
                                kotlinx.coroutines.GlobalScope.launch {
                                    delay(1000)
                                    viewModel.resetService()
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val newX = (serviceX + dragAmount.x).coerceIn(
                                    0f,
                                    (screenWidth - 100).toFloat()
                                )
                                val newY = (serviceY + dragAmount.y).coerceIn(
                                    0f,
                                    (screenHeight - 100).toFloat()
                                )
                                viewModel.updateServicePosition(newX, newY)
                            }
                        )
                    },
                contentScale = ContentScale.Fit
            )
        }

        // 四個角色圖示
        if (screenHeight > 0) {
            val roleSizeDp = with(density) { 300.toDp() }
            val yOffsetDp = with(density) { (screenHeight / 2 - 300).toDp() }

            // 左上角 - 嬰幼兒 (role0)
            Image(
                painter = painterResource(id = R.drawable.role0),
                contentDescription = "嬰幼兒",
                modifier = Modifier
                    .size(roleSizeDp)
                    .align(Alignment.TopStart)
                    .offset(y = yOffsetDp),
                contentScale = ContentScale.Fit
            )

            // 右上角 - 兒童 (role1)
            Image(
                painter = painterResource(id = R.drawable.role1),
                contentDescription = "兒童",
                modifier = Modifier
                    .size(roleSizeDp)
                    .align(Alignment.TopEnd)
                    .offset(y = yOffsetDp),
                contentScale = ContentScale.Fit
            )

            // 左下角 - 成人 (role2)
            Image(
                painter = painterResource(id = R.drawable.role2),
                contentDescription = "成人",
                modifier = Modifier
                    .size(roleSizeDp)
                    .align(Alignment.BottomStart),
                contentScale = ContentScale.Fit
            )

            // 右下角 - 一般民眾 (role3)
            Image(
                painter = painterResource(id = R.drawable.role3),
                contentDescription = "一般民眾",
                modifier = Modifier
                    .size(roleSizeDp)
                    .align(Alignment.BottomEnd),
                contentScale = ContentScale.Fit
            )
        }
    }
}