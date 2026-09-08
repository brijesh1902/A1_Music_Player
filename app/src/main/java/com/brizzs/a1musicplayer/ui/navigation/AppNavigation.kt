package com.brizzs.a1musicplayer.ui.navigation

import android.content.Intent
import android.widget.ImageView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.brizzs.a1musicplayer.R
import com.brizzs.a1musicplayer.ui.main.MainScreen
import com.brizzs.a1musicplayer.ui.main.MainViewModel
import com.brizzs.a1musicplayer.ui.playing.PlayActivity
import com.brizzs.a1musicplayer.utils.MyApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            LaunchedEffect(Unit) {
                delay(1200)
                val destination = if (MyApplication.getSharePreference().isFirstRun) {
                    "onboarding"
                } else {
                    "main"
                }
                navController.navigate(destination) {
                    popUpTo("splash") { inclusive = true }
                }
            }
            SplashScreenView()
        }

        composable("onboarding") {
            OnboardingScreenView(
                onFinished = {
                    MyApplication.getSharePreference().isFirstRun = false
                    navController.navigate("main") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(viewModel = viewModel) { songPath ->
                val context = navController.context
                val intent = Intent(context, PlayActivity::class.java).apply {
                    putExtra("path", songPath)
                }
                context.startActivity(intent)
            }
        }
    }
}

@Composable
fun SplashScreenView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.size(160.dp),
            shape = RoundedCornerShape(80.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6200EE)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.headphones),
                    contentDescription = "Headphones Logo",
                    modifier = Modifier.size(120.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreenView(onFinished: () -> Unit) {
    val pages = listOf(
        Triple(R.drawable.img_onboard1, "Discover Great Music", "Explore millions of tracks instantly with lightning-fast search."),
        Triple(R.drawable.img_onboard2, "Organize Your Library", "Browse and play your music seamlessly by albums, artists, and playlists."),
        Triple(R.drawable.img_onboard3, "Enjoy Anywhere", "Support for background play, headset controls, and volume gestures.")
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6200EE)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                factory = { ctx ->
                    ImageView(ctx).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                },
                update = { imageView ->
                    Glide.with(imageView.context)
                        .load(R.drawable.background_onboard)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView)
                },
                modifier = Modifier.fillMaxSize()
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { ctx ->
                            ImageView(ctx).apply {
                                scaleType = ImageView.ScaleType.FIT_CENTER
                            }
                        },
                        update = { imageView ->
                            Glide.with(imageView.context)
                                .load(pages[page].first)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                repeat(pages.size) { index ->
                    val color = if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.5f)
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color, shape = CircleShape)
                    )
                }
            }

            Text(
                text = pages[pagerState.currentPage].second,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp)
            )

            Text(
                text = pages[pagerState.currentPage].third,
                color = Color.White,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.6f)
                    .padding(horizontal = 24.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            FloatingActionButton(
                onClick = {
                    if (pagerState.currentPage + 1 < pages.size) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinished()
                    }
                },
                containerColor = Color.White,
                contentColor = Color(0xFF6200EE),
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play_24),
                    contentDescription = "Next",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
