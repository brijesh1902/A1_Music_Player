package com.brizzs.a1musicplayer.ui.main

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.brizz.linguistic.presentation.components.translated
import com.brizzs.a1musicplayer.R
import com.brizzs.a1musicplayer.model.Album
import com.brizzs.a1musicplayer.model.Artist
import com.brizzs.a1musicplayer.model.Songs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onSongClick: (String) -> Unit = {}
) {
    val songs by viewModel.songsLiveData.observeAsState(initial = emptyList())
    val albums by viewModel.albumLiveData.observeAsState(initial = emptyList())
    val artists by viewModel.artistLiveData.observeAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Recently, 1: Albums, 2: Artists, 3: Playlists
    var isGridView by remember { mutableStateOf(false) }

    val filteredSongs = songs.filter {
        (it.name?.contains(searchQuery, ignoreCase = true) == true) ||
        (it.artist?.contains(searchQuery, ignoreCase = true) == true)
    }

    val filteredAlbums = albums.filter {
        (it.album?.contains(searchQuery, ignoreCase = true) == true) ||
        (it.artist?.contains(searchQuery, ignoreCase = true) == true)
    }

    val filteredArtists = artists.filter {
        (it.artist?.contains(searchQuery, ignoreCase = true) == true)
    }

    Scaffold(
        topBar = {
            Surface(
                color = Color(0xFF3C3C3C),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search...", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, color = Color.Black)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            painter = painterResource(
                                id = if (isGridView) R.drawable.ic_baseline_view_list_24 else R.drawable.ic_baseline_grid_view_24
                            ),
                            contentDescription = "Toggle View",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(onClick = { /* Handle settings */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_menu_24),
                            contentDescription = "Menu",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (songs.isNotEmpty()) {
                MiniPlayerBar(song = songs.first()) { songPath ->
                    onSongClick(songPath)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x60BDBDBD))
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                indicator = {}
            ) {
                val tabs = listOf("Recently", "Albums", "Artists", "Playlists")
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selectedTab == index) Color(0xFFFF9800) else Color.DarkGray)
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTab) {
                0 -> {
                    if (filteredSongs.isEmpty()) {
                        EmptyStateView("No songs found")
                    } else {
                        SongContentList(songs = filteredSongs, isGridView = isGridView) { path -> onSongClick(path) }
                    }
                }
                1 -> {
                    if (filteredAlbums.isEmpty()) {
                        EmptyStateView("No albums found")
                    } else {
                        AlbumContentList(albums = filteredAlbums)
                    }
                }
                2 -> {
                    if (filteredArtists.isEmpty()) {
                        EmptyStateView("No artists found")
                    } else {
                        ArtistContentList(artists = filteredArtists)
                    }
                }
                3 -> {
                    EmptyStateView("No playlists created yet")
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(messageStr: String) {
    val message by messageStr.translated()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
    }
}

@Composable
fun SongContentList(songs: List<Songs>, isGridView: Boolean, onSongClick: (String) -> Unit) {
    if (isGridView) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(songs) { song ->
                SongGridCard(song = song) { path -> onSongClick(path) }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(songs) { song ->
                SongListRowCard(song = song) { path -> onSongClick(path) }
            }
        }
    }
}

@Composable
fun AlbumContentList(albums: List<Album>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(albums) { album ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AndroidView(
                        factory = { ctx -> ImageView(ctx).apply { scaleType = ImageView.ScaleType.CENTER_CROP } },
                        update = { view ->
                            Glide.with(view.context).load(album.image).placeholder(R.mipmap.ic_launcher).into(view)
                        },
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = album.album ?: "Unknown Album", fontWeight = FontWeight.Bold, maxLines = 1, color = Color(0xFF4DA0FF))
                        Text(text = album.artist ?: "Unknown Artist", fontSize = 12.sp, color = Color.DarkGray, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun ArtistContentList(artists: List<Artist>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(artists) { artist ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AndroidView(
                        factory = { ctx -> ImageView(ctx).apply { scaleType = ImageView.ScaleType.CENTER_CROP } },
                        update = { view ->
                            Glide.with(view.context).load(artist.image).placeholder(R.mipmap.ic_launcher).into(view)
                        },
                        modifier = Modifier.size(50.dp).clip(RoundedCornerShape(25.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = artist.artist ?: "Unknown Artist", fontWeight = FontWeight.Bold, color = Color(0xFF4DA0FF))
                        Text(text = artist.songName ?: "", fontSize = 12.sp, color = Color.DarkGray, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun MiniPlayerBar(song: Songs, onClick: (String) -> Unit) {
    Surface(
        color = Color(0xFF3C3C3C),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { song.data?.let { onClick(it) } }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
                progress = { 0.3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color(0xFF4DA0FF),
                trackColor = Color.White
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AndroidView(
                        factory = { ctx -> ImageView(ctx).apply { scaleType = ImageView.ScaleType.CENTER_CROP } },
                        update = { view ->
                            Glide.with(view.context).load(song.image).placeholder(R.mipmap.ic_launcher).into(view)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = song.name ?: "Title",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = song.artist ?: "Artist",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { /* Play / Pause */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play_24),
                        contentDescription = "Play/Pause",
                        tint = Color.White
                    )
                }

                IconButton(onClick = { /* Next */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_skip_next_24),
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SongListRowCard(song: Songs, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { song.data?.let { onClick(it) } },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AndroidView(
                factory = { ctx ->
                    ImageView(ctx).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                },
                update = { imageView ->
                    Glide.with(imageView.context)
                        .load(song.image)
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView)
                },
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.name ?: "Unknown Song",
                    color = Color(0xFF4DA0FF),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.artist ?: "Unknown Artist",
                    color = Color.Black,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SongGridCard(song: Songs, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { song.data?.let { onClick(it) } },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AndroidView(
                factory = { ctx ->
                    ImageView(ctx).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                },
                update = { imageView ->
                    Glide.with(imageView.context)
                        .load(song.image)
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = song.name ?: "Unknown Song",
                    color = Color(0xFF4DA0FF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.artist ?: "Unknown Artist",
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
