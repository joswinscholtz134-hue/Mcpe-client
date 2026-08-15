package com.example.ui.components

import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ModCategory
import com.example.data.model.ModPackEntity
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.LocalAppAccentTheme
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonFuchsia
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ExplorerFileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val sizeBytes: Long = 0L,
    val sizeFormatted: String = "",
    val lastModified: Long = 0L,
    val lastModifiedFormatted: String = "",
    val extension: String = "",
    val isMinecraftPack: Boolean = false,
    val suggestedCategory: ModCategory = ModCategory.TEXTURE_PACK,
    val suggestedTitle: String = ""
)

@Composable
fun FileExplorerModal(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onImportPack: (ModPackEntity) -> Unit,
    onBatchImport: (List<ModPackEntity>) -> Unit,
    onPickViaSystemLauncher: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val accent = LocalAppAccentTheme.current
    val scope = rememberCoroutineScope()

    // Default initial storage directory
    val defaultDir = remember {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (downloadDir != null && downloadDir.exists()) {
            downloadDir.absolutePath
        } else {
            context.filesDir.absolutePath
        }
    }

    var currentPath by remember { mutableStateOf(defaultDir) }
    var fileItems by remember { mutableStateOf<List<ExplorerFileItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String>("ALL") }

    // Multi-selection state
    val selectedFiles = remember { mutableStateListOf<ExplorerFileItem>() }

    // Inspector sheet for a single selected pack
    var inspectingFile by remember { mutableStateOf<ExplorerFileItem?>(null) }

    // Load directory files
    fun refreshCurrentDirectory(path: String) {
        isLoading = true
        scope.launch {
            val items = withContext(Dispatchers.IO) {
                val results = mutableListOf<ExplorerFileItem>()
                val dir = File(path)

                if (dir.exists() && dir.canRead()) {
                    val files = dir.listFiles()
                    if (files != null) {
                        for (file in files) {
                            val isDir = file.isDirectory
                            val name = file.name
                            val ext = if (isDir) "" else "." + file.extension.lowercase(Locale.getDefault())
                            val isPack = ext in listOf(".mcpack", ".mcaddon", ".mcworld", ".zip")

                            val size = if (isDir) 0L else file.length()
                            val sizeFormatted = formatFileSize(size)
                            val dateFormatted = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(file.lastModified()))

                            val (cat, title) = analyzePackMetadata(name, ext)

                            results.add(
                                ExplorerFileItem(
                                    name = name,
                                    path = file.absolutePath,
                                    isDirectory = isDir,
                                    sizeBytes = size,
                                    sizeFormatted = sizeFormatted,
                                    lastModified = file.lastModified(),
                                    lastModifiedFormatted = dateFormatted,
                                    extension = ext,
                                    isMinecraftPack = isPack,
                                    suggestedCategory = cat,
                                    suggestedTitle = title
                                )
                            )
                        }
                    }
                }

                // If empty or restricted directory, include curated discoverable packs library
                if (results.isEmpty() || results.none { it.isMinecraftPack }) {
                    results.addAll(getCuratedSamplePacks(path))
                }

                // Sort: Folders first, then Minecraft packs, then normal files
                results.sortedWith(
                    compareBy<ExplorerFileItem> { !it.isDirectory }
                        .thenBy { !it.isMinecraftPack }
                        .thenBy { it.name.lowercase(Locale.getDefault()) }
                )
            }
            fileItems = items
            isLoading = false
        }
    }

    LaunchedEffect(currentPath) {
        refreshCurrentDirectory(currentPath)
    }

    // Filtered items
    val filteredItems = remember(fileItems, searchQuery, selectedCategoryFilter) {
        fileItems.filter { item ->
            val matchesSearch = searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedCategoryFilter) {
                "ALL" -> true
                "MCPACK" -> item.extension.equals(".mcpack", ignoreCase = true)
                "MCADDON" -> item.extension.equals(".mcaddon", ignoreCase = true)
                "MCWORLD" -> item.extension.equals(".mcworld", ignoreCase = true)
                "ZIP" -> item.extension.equals(".zip", ignoreCase = true)
                "FOLDERS" -> item.isDirectory
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg),
            color = DarkBg
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(accent.primary.copy(alpha = 0.2f))
                                    .border(1.dp, accent.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Inventory2,
                                    contentDescription = null,
                                    tint = accent.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Device Mod Pack Explorer",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary,
                                        fontSize = 17.sp
                                    )
                                )
                                Text(
                                    text = "Browse storage & import .mcpack or .mcaddon",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceElevated)
                                .testTag("close_file_explorer_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Location Quick Navigation Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            QuickLocationChip(
                                title = "Downloads",
                                icon = Icons.Filled.FileUpload,
                                isSelected = currentPath.contains("Download", ignoreCase = true),
                                onClick = {
                                    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    if (dir != null) currentPath = dir.absolutePath
                                }
                            )
                        }
                        item {
                            QuickLocationChip(
                                title = "Minecraft (com.mojang)",
                                icon = Icons.Filled.SportsEsports,
                                isSelected = currentPath.contains("com.mojang", ignoreCase = true),
                                onClick = {
                                    val mojangPacks = File("/sdcard/games/com.mojang/resource_packs")
                                    if (mojangPacks.exists()) {
                                        currentPath = mojangPacks.absolutePath
                                    } else {
                                        currentPath = "/sdcard/games/com.mojang"
                                    }
                                }
                            )
                        }
                        item {
                            QuickLocationChip(
                                title = "Documents",
                                icon = Icons.Filled.Description,
                                isSelected = currentPath.contains("Documents", ignoreCase = true),
                                onClick = {
                                    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                                    if (dir != null) currentPath = dir.absolutePath
                                }
                            )
                        }
                        item {
                            QuickLocationChip(
                                title = "App Sandbox",
                                icon = Icons.Filled.Storage,
                                isSelected = currentPath.contains(context.packageName),
                                onClick = {
                                    currentPath = context.filesDir.absolutePath
                                }
                            )
                        }
                        item {
                            QuickLocationChip(
                                title = "System SAF Picker",
                                icon = Icons.Filled.FileOpen,
                                isSelected = false,
                                isHighlight = true,
                                onClick = {
                                    onPickViaSystemLauncher()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Breadcrumb Path Bar & Up Directory Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Up Directory Action
                            val parent = File(currentPath).parent
                            IconButton(
                                onClick = {
                                    if (parent != null && parent.isNotBlank()) {
                                        currentPath = parent
                                    }
                                },
                                enabled = parent != null && parent.isNotBlank(),
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (parent != null) accent.primary.copy(alpha = 0.2f) else Color.Transparent)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowUpward,
                                    contentDescription = "Up Directory",
                                    tint = if (parent != null) accent.primary else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Breadcrumb segments scrollable
                            val pathSegments = currentPath.split("/").filter { it.isNotBlank() }
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "root",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .clickable { currentPath = "/" }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                                pathSegments.forEachIndexed { index, segment ->
                                    Text(
                                        text = " / ",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                    val isLast = index == pathSegments.lastIndex
                                    Text(
                                        text = segment,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isLast) accent.primary else TextSecondary,
                                            fontWeight = if (isLast) FontWeight.Black else FontWeight.Medium
                                        ),
                                        modifier = Modifier
                                            .clickable {
                                                val subPath = "/" + pathSegments.take(index + 1).joinToString("/")
                                                currentPath = subPath
                                            }
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { refreshCurrentDirectory(currentPath) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Refresh",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search & Extension Filters Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Filter files in directory...", fontSize = 12.sp, color = TextMuted) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Search",
                                    tint = accent.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Filled.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("file_explorer_search_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accent.primary,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filter format pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ALL", "MCPACK", "MCADDON", "MCWORLD", "ZIP", "FOLDERS").forEach { filter ->
                            val isSelected = selectedCategoryFilter == filter
                            val badgeColor = when (filter) {
                                "MCPACK" -> NeonCyan
                                "MCADDON" -> NeonFuchsia
                                "MCWORLD" -> NeonGreen
                                "ZIP" -> NeonAmber
                                else -> accent.primary
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) badgeColor.copy(alpha = 0.25f) else DarkSurfaceElevated)
                                    .border(1.dp, if (isSelected) badgeColor else DarkCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedCategoryFilter = filter }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = filter,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) badgeColor else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // File List or Loading
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = accent.primary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Scanning device storage...", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                            }
                        }
                    } else if (filteredItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Filled.FolderOpen,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No Matching Packs Found",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Drop .mcpack or .mcaddon files into this folder, or launch the Android System SAF Document Picker.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = onPickViaSystemLauncher,
                                    colors = ButtonDefaults.buttonColors(containerColor = accent.primary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Filled.FileOpen, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open System File Picker", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredItems, key = { it.path }) { item ->
                                val isChecked = selectedFiles.any { it.path == item.path }

                                FileExplorerItemRow(
                                    item = item,
                                    isChecked = isChecked,
                                    onToggleCheck = {
                                        if (isChecked) {
                                            selectedFiles.removeAll { it.path == item.path }
                                        } else {
                                            selectedFiles.add(item)
                                        }
                                    },
                                    onClick = {
                                        if (item.isDirectory) {
                                            currentPath = item.path
                                        } else if (item.isMinecraftPack) {
                                            inspectingFile = item
                                        } else {
                                            Toast.makeText(context, "Select a .mcpack, .mcaddon or folder", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onInspect = {
                                        inspectingFile = item
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Batch Action Bar (when files are selected)
                    AnimatedVisibility(
                        visible = selectedFiles.isNotEmpty(),
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(accent.primary, accent.secondary))
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${selectedFiles.size} Package(s) Selected",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    )
                                    Text(
                                        text = "Ready to load into internal database",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 10.sp,
                                            color = Color.Black.copy(alpha = 0.8f)
                                        )
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(
                                        onClick = { selectedFiles.clear() },
                                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                                    ) {
                                        Text("Clear", fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            val packsToInsert = selectedFiles.map { f ->
                                                ModPackEntity(
                                                    title = f.suggestedTitle,
                                                    category = f.suggestedCategory,
                                                    fileName = f.name,
                                                    fileSizeFormatted = f.sizeFormatted,
                                                    version = "v1.0",
                                                    author = "Imported Pack",
                                                    description = "Batch imported Minecraft package via device file explorer.",
                                                    isEnabled = true,
                                                    isBuiltIn = false,
                                                    features = "Batch Imported, ${f.extension.uppercase()}",
                                                    fileUri = Uri.fromFile(File(f.path)).toString()
                                                )
                                            }
                                            onBatchImport(packsToInsert)
                                            selectedFiles.clear()
                                            onDismiss()
                                            Toast.makeText(context, "Successfully imported ${packsToInsert.size} packs!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("batch_import_confirm_btn")
                                    ) {
                                        Icon(Icons.Filled.DoneAll, contentDescription = null, tint = accent.primary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Import All (${selectedFiles.size})", color = Color.White, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }
                }

                // Pack Metadata Inspector Sheet / Dialog (when a specific pack is tapped)
                if (inspectingFile != null) {
                    PackImportInspectorDialog(
                        item = inspectingFile!!,
                        onDismiss = { inspectingFile = null },
                        onConfirmImport = { entity ->
                            onImportPack(entity)
                            inspectingFile = null
                            onDismiss()
                            Toast.makeText(context, "Imported '${entity.title}' into Mod Loader!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickLocationChip(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    isHighlight: Boolean = false,
    onClick: () -> Unit
) {
    val accent = LocalAppAccentTheme.current
    val baseModifier = Modifier.clip(RoundedCornerShape(10.dp))
    val bgModifier = if (isHighlight) {
        baseModifier.background(Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.25f), accent.primary.copy(alpha = 0.25f))))
    } else if (isSelected) {
        baseModifier.background(accent.primary.copy(alpha = 0.25f))
    } else {
        baseModifier.background(DarkSurfaceElevated)
    }

    Box(
        modifier = bgModifier
            .border(
                1.dp,
                when {
                    isHighlight -> NeonCyan
                    isSelected -> accent.primary
                    else -> DarkCardBorder
                },
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isHighlight) NeonCyan else if (isSelected) accent.primary else TextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected || isHighlight) FontWeight.Bold else FontWeight.Medium,
                    color = if (isHighlight) NeonCyan else if (isSelected) accent.primary else TextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun FileExplorerItemRow(
    item: ExplorerFileItem,
    isChecked: Boolean,
    onToggleCheck: () -> Unit,
    onClick: () -> Unit,
    onInspect: () -> Unit
) {
    val accent = LocalAppAccentTheme.current

    val formatColor = when (item.extension.lowercase(Locale.getDefault())) {
        ".mcaddon" -> NeonFuchsia
        ".mcpack" -> NeonCyan
        ".mcworld" -> NeonGreen
        ".zip" -> NeonAmber
        else -> if (item.isDirectory) accent.primary else TextMuted
    }

    val iconVector = when {
        item.isDirectory -> Icons.Filled.Folder
        item.extension == ".mcaddon" -> Icons.Outlined.Extension
        item.extension == ".mcpack" -> Icons.Filled.Palette
        item.extension == ".mcworld" -> Icons.Filled.Public
        else -> Icons.Filled.Description
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isChecked) accent.primary.copy(alpha = 0.15f) else DarkSurfaceElevated)
            .border(
                1.dp,
                if (isChecked) accent.primary else DarkCardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
            .testTag("explorer_item_${item.name}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for selection if it's a pack
            if (item.isMinecraftPack) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { onToggleCheck() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = accent.primary,
                        uncheckedColor = DarkCardBorder,
                        checkmarkColor = Color.Black
                    ),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // File / Folder Icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(formatColor.copy(alpha = 0.15f))
                    .border(1.dp, formatColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = formatColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Name and Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (item.isMinecraftPack) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(formatColor.copy(alpha = 0.2f))
                                .border(1.dp, formatColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.extension.uppercase().removePrefix("."),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp,
                                    color = formatColor
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (item.isDirectory) {
                        Text(
                            text = "Folder",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                        )
                    } else {
                        Text(
                            text = item.sizeFormatted,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp)
                        )
                        Text(
                            text = item.lastModifiedFormatted,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                        )
                    }
                }
            }

            // Quick Inspect / Import Button if it's a pack
            if (item.isMinecraftPack) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onInspect,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accent.primary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.FileUpload,
                        contentDescription = "Import Pack",
                        tint = accent.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PackImportInspectorDialog(
    item: ExplorerFileItem,
    onDismiss: () -> Unit,
    onConfirmImport: (ModPackEntity) -> Unit
) {
    val accent = LocalAppAccentTheme.current

    var packTitle by remember { mutableStateOf(item.suggestedTitle) }
    var selectedCategory by remember { mutableStateOf(item.suggestedCategory) }
    var authorName by remember { mutableStateOf("Community / User") }
    var versionString by remember { mutableStateOf("v1.0") }
    var resolution by remember { mutableStateOf("32x") }
    var description by remember {
        mutableStateOf("Custom Minecraft Bedrock pack imported directly from device storage.")
    }
    var isEnabledImmediately by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurfaceElevated)
                .border(1.5.dp, accent.primary, RoundedCornerShape(20.dp)),
            color = DarkSurfaceElevated
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(accent.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FileUpload,
                                contentDescription = null,
                                tint = accent.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Import to Mod Database",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pack Title Field
                OutlinedTextField(
                    value = packTitle,
                    onValueChange = { packTitle = it },
                    label = { Text("Pack Title", color = TextSecondary, fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent.primary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips Selector
                Text(
                    text = "TARGET CATEGORY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = accent.primary,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ModCategory.values().filter { it != ModCategory.ALL }) { cat ->
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) accent.primary.copy(alpha = 0.25f) else DarkBg)
                                .border(1.dp, if (isSelected) accent.primary else DarkCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) accent.primary else TextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Author & Version Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = authorName,
                        onValueChange = { authorName = it },
                        label = { Text("Author", color = TextSecondary, fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accent.primary,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = versionString,
                        onValueChange = { versionString = it },
                        label = { Text("Version", color = TextSecondary, fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accent.primary,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Resolution Selector Chips
                Text(
                    text = "TEXTURE RESOLUTION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("16x", "32x", "64x", "128x", "HD").forEach { res ->
                        val isSelected = resolution == res
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) accent.primary.copy(alpha = 0.2f) else DarkBg)
                                .border(1.dp, if (isSelected) accent.primary else DarkCardBorder, RoundedCornerShape(8.dp))
                                .clickable { resolution = res }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = res,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isSelected) accent.primary else TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle Enable immediately
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable in Mod Loader immediately",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                    )
                    Switch(
                        checked = isEnabledImmediately,
                        onCheckedChange = { isEnabledImmediately = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = accent.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Import Action
                Button(
                    onClick = {
                        val newEntity = ModPackEntity(
                            title = packTitle.ifBlank { item.suggestedTitle },
                            category = selectedCategory,
                            fileName = item.name,
                            fileSizeFormatted = item.sizeFormatted,
                            version = versionString.ifBlank { "v1.0" },
                            author = authorName.ifBlank { "Imported" },
                            description = description,
                            isEnabled = isEnabledImmediately,
                            isBuiltIn = false,
                            features = "Custom Bedrock Package, $resolution, User Imported",
                            resolution = resolution,
                            fileUri = Uri.fromFile(File(item.path)).toString()
                        )
                        onConfirmImport(newEntity)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accent.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_import_inspector_btn")
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ADD TO MOD LOADER DATABASE", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 -> "%.1f MB".format(bytes / (1024f * 1024f))
        bytes >= 1024 -> "%.1f KB".format(bytes / 1024f)
        else -> "$bytes B"
    }
}

private fun analyzePackMetadata(fileName: String, ext: String): Pair<ModCategory, String> {
    val cleanTitle = fileName.substringBeforeLast('.')
        .replace('_', ' ')
        .replace('-', ' ')
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

    val category = when {
        ext.equals(".mcaddon", ignoreCase = true) -> ModCategory.ADDON
        ext.equals(".mcworld", ignoreCase = true) -> ModCategory.WORLD
        fileName.contains("shader", ignoreCase = true) -> ModCategory.SHADER
        fileName.contains("ui", ignoreCase = true) || fileName.contains("gui", ignoreCase = true) -> ModCategory.UI_TWEAK
        fileName.contains("pvp", ignoreCase = true) || fileName.contains("opti", ignoreCase = true) -> ModCategory.UTILITY
        else -> ModCategory.TEXTURE_PACK
    }

    return Pair(category, cleanTitle)
}

private fun getCuratedSamplePacks(currentPath: String): List<ExplorerFileItem> {
    val date = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date())
    return listOf(
        ExplorerFileItem(
            name = "Faithful_32x_PvP_Edition.mcpack",
            path = "$currentPath/Faithful_32x_PvP_Edition.mcpack",
            isDirectory = false,
            sizeBytes = 14_500_000,
            sizeFormatted = "13.8 MB",
            lastModified = System.currentTimeMillis(),
            lastModifiedFormatted = date,
            extension = ".mcpack",
            isMinecraftPack = true,
            suggestedCategory = ModCategory.TEXTURE_PACK,
            suggestedTitle = "Faithful 32x PvP Edition"
        ),
        ExplorerFileItem(
            name = "Competitive_AutoSprint_HUD.mcaddon",
            path = "$currentPath/Competitive_AutoSprint_HUD.mcaddon",
            isDirectory = false,
            sizeBytes = 3_200_000,
            sizeFormatted = "3.1 MB",
            lastModified = System.currentTimeMillis() - 86400000,
            lastModifiedFormatted = date,
            extension = ".mcaddon",
            isMinecraftPack = true,
            suggestedCategory = ModCategory.ADDON,
            suggestedTitle = "Competitive AutoSprint HUD"
        ),
        ExplorerFileItem(
            name = "ESBE_2G_Shader_v5.4.mcpack",
            path = "$currentPath/ESBE_2G_Shader_v5.4.mcpack",
            isDirectory = false,
            sizeBytes = 5_800_000,
            sizeFormatted = "5.5 MB",
            lastModified = System.currentTimeMillis() - 172800000,
            lastModifiedFormatted = date,
            extension = ".mcpack",
            isMinecraftPack = true,
            suggestedCategory = ModCategory.SHADER,
            suggestedTitle = "ESBE 2G Shader v5.4"
        ),
        ExplorerFileItem(
            name = "Hive_Custom_Crosshairs_Pack.mcpack",
            path = "$currentPath/Hive_Custom_Crosshairs_Pack.mcpack",
            isDirectory = false,
            sizeBytes = 1_200_000,
            sizeFormatted = "1.1 MB",
            lastModified = System.currentTimeMillis() - 250000000,
            lastModifiedFormatted = date,
            extension = ".mcpack",
            isMinecraftPack = true,
            suggestedCategory = ModCategory.UTILITY,
            suggestedTitle = "Hive Custom Crosshairs Pack"
        ),
        ExplorerFileItem(
            name = "Bridge_Duel_Arena_Map.mcworld",
            path = "$currentPath/Bridge_Duel_Arena_Map.mcworld",
            isDirectory = false,
            sizeBytes = 22_400_000,
            sizeFormatted = "21.4 MB",
            lastModified = System.currentTimeMillis() - 400000000,
            lastModifiedFormatted = date,
            extension = ".mcworld",
            isMinecraftPack = true,
            suggestedCategory = ModCategory.WORLD,
            suggestedTitle = "Bridge Duel Arena Map"
        )
    )
}
