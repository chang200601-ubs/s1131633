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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExamScreen(viewModel: ExamViewModel) {
    val screenWidth by viewModel.screenWidth.collectAsState()
    val screenHeight by viewModel.screenHeight.collectAsState()

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

            // 兩行文字緊貼換行
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "瑪利亞基金會服務大考驗",
                    color = Color.Black,
                    fontSize = 18.sp,
                )

                Text(
                    text = "作者：資管二B 張伊傑",
                    color = Color.Black,
                    fontSize = 18.sp
                )
            }
        }

        // 底部顯示螢幕尺寸資訊
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "螢幕大小：$screenWidth × $screenHeight",
                color = Color.Black,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "成績：0分",
                color = Color.Black,
                fontSize = 16.sp
            )
        }
    }
}