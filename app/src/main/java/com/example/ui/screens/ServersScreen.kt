package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ServerEntity
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LocalAppAccentTheme
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.MinecraftLauncher

@Composable
fun ServersScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accent = LocalAppAccentTheme.current
    val servers by viewModel.allServers.collectAsStateWithLifecycle()
    val isPingingAll by viewModel.isPingingAll.collectAsStateWithLifecycle()

    var showAddServerDialog by remember { mutableStateOf(false) }
    var serverToDelete by remember { mutableStateOf<ServerEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(
                title = "Bedrock Servers",
                subtitle = "Manage networks, check live ping & 1-tap connect",
                action = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Ping All Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
                                .clickable(enabled = !isPingingAll) { viewModel.pingAllServers() }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                                .testTag("ping_all_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isPingingAll) {
                                    CircularProgressIndicator(
                                        color = accent.primary,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = "Ping",
                                        tint = accent.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isPingingAll) "PINGING..." else "PING ALL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = accent.primary
                                    )
                                )
                            }
                        }

                        // Add Server Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(accent.primary, accent.secondary))
                                )
                                .clickable { showAddServerDialog = true }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                                .testTag("add_server_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add",
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ADD SERVER",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }
            )
        }

        items(servers, key = { it.id }) { server ->
            ServerCard(
                server = server,
                onConnect = {
                    MinecraftLauncher.connectToServer(context, server.name, server.ip, server.port)
                },
                onCopyIp = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Server IP", "${server.ip}:${server.port}")
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied ${server.ip}:${server.port}", Toast.LENGTH_SHORT).show()
                },
                onToggleFavorite = { viewModel.toggleServerFavorite(server) },
                onPing = { viewModel.pingSingleServer(server) },
                onDelete = { serverToDelete = server }
            )
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }

    // Add Custom Server Dialog
    if (showAddServerDialog) {
        var serverName by remember { mutableStateOf("") }
        var serverIp by remember { mutableStateOf("") }
        var serverPort by remember { mutableStateOf("19132") }
        var serverDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddServerDialog = false },
            containerColor = DarkSurfaceElevated,
            title = {
                Text(
                    text = "Add Bedrock Server",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = serverName,
                        onValueChange = { serverName = it },
                        label = { Text("Server Name", color = TextMuted) },
                        placeholder = { Text("e.g. My Survival Realm", color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accent.primary,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = serverIp,
                        onValueChange = { serverIp = it },
                        label = { Text("Server Address / IP", color = TextMuted) },
                        placeholder = { Text("e.g. play.myserver.net", color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accent.primary,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = serverPort,
                        onValueChange = { serverPort = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Port (Bedrock Default: 19132)", color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accent.primary,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = serverDesc,
                        onValueChange = { serverDesc = it },
                        label = { Text("Description (Optional)", color = TextMuted) },
                        placeholder = { Text("e.g. Custom SMP with friends", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accent.primary,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (serverIp.isNotBlank()) {
                            val portInt = serverPort.toIntOrNull() ?: 19132
                            viewModel.addCustomServer(serverName, serverIp, portInt, serverDesc)
                            showAddServerDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
                ) {
                    Text("Save Server", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddServerDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (serverToDelete != null) {
        val server = serverToDelete!!
        AlertDialog(
            onDismissRequest = { serverToDelete = null },
            containerColor = DarkSurfaceElevated,
            title = { Text("Delete Server?", color = TextPrimary) },
            text = {
                Text("Are you sure you want to remove '${server.name}' from your server list?", color = TextSecondary)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteServer(server)
                        serverToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { serverToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun ServerCard(
    server: ServerEntity,
    onConnect: () -> Unit,
    onCopyIp: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPing: () -> Unit,
    onDelete: () -> Unit
) {
    val accent = LocalAppAccentTheme.current

    val pingColor = when {
        server.pingMs in 1..60 -> StatusOnline
        server.pingMs in 61..120 -> StatusWarning
        server.pingMs > 120 -> StatusOffline
        else -> TextMuted
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (server.isFavorite) accent.primary.copy(alpha = 0.5f) else DarkCardBorder
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Name, Favorite, Ping Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                accent.primary.copy(alpha = 0.2f),
                                RoundedCornerShape(10.dp)
                            )
                            .border(1.dp, accent.primary.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Dns,
                            contentDescription = null,
                            tint = accent.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = server.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            if (server.isFeatured) {
                                Spacer(modifier = Modifier.width(6.dp))
                                NeonBadge(text = "FEATURED", color = accent.secondary)
                            }
                        }
                        Text(
                            text = "${server.ip}:${server.port}",
                            style = MaterialTheme.typography.labelSmall.copy(color = accent.primary)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Ping pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(pingColor.copy(alpha = 0.15f))
                            .border(0.8.dp, pingColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { onPing() }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(pingColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (server.pingMs > 0) "${server.pingMs}ms" else "Ping",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = pingColor
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (server.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (server.isFavorite) NeonCrimson else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (server.motd.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = server.motd.replace("§[0-9a-fk-or]".toRegex(), ""),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    ),
                    maxLines = 2
                )
            }

            if (server.gameModes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    server.gameModes.split(",").take(3).forEach { mode ->
                        Box(
                            modifier = Modifier
                                .background(DarkSurfaceElevated, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = mode.trim(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            )
                        }
                    }
                    if (server.onlinePlayers > 0) {
                        Text(
                            text = "${server.onlinePlayers} online",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = StatusOnline
                            ),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable { onCopyIp() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copy IP",
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Copy IP",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }
                    }

                    if (!server.isFeatured) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // 1-Tap Connect
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(listOf(accent.primary, accent.secondary))
                        )
                        .clickable { onConnect() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Connect",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CONNECT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        )
                    }
                }
            }
        }
    }
}
