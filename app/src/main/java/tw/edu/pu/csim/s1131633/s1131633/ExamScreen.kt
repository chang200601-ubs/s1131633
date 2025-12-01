package tw.edu.pu.csim.s1131633.s1131633

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExamScreen(viewModel: ExamViewModel) {
    val screenWidth by viewModel.screenWidth.collectAsState()
    val screenHeight by viewModel.screenHeight.collectAsState()
    val density = LocalDensity.current

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
                text = "成績：0分",
                color = Color.Black,
                fontSize = 14.sp
            )
        }

        // 四個角色圖示
        if (screenHeight > 0) {
            val roleSizeDp = with(density) { 300.toDp() }
            val yOffsetDp = with(density) { (screenHeight / 2 - 300).toDp() }

            // 左上角 - 嬰幼兒 (role0) - 下方切齊螢幕高度 1/2
            Image(
                painter = painterResource(id = R.drawable.role0),
                contentDescription = "嬰幼兒",
                modifier = Modifier
                    .size(roleSizeDp)
                    .align(Alignment.TopStart)
                    .offset(y = yOffsetDp),
                contentScale = ContentScale.Fit
            )

            // 右上角 - 兒童 (role1) - 下方切齊螢幕高度 1/2
            Image(
                painter = painterResource(id = R.drawable.role1),
                contentDescription = "兒童",
                modifier = Modifier
                    .size(roleSizeDp)
                    .align(Alignment.TopEnd)
                    .offset(y = yOffsetDp),
                contentScale = ContentScale.Fit
            )

            // 左下角 - 成人 (role2) - 完全貼齊底部
            Image(
                painter = painterResource(id = R.drawable.role2),
                contentDescription = "成人",
                modifier = Modifier
                    .size(roleSizeDp)
                    .align(Alignment.BottomStart),
                contentScale = ContentScale.Fit
            )

            // 右下角 - 一般民眾 (role3) - 完全貼齊底部
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