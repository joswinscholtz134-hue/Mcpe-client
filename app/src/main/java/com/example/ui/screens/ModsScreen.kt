package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.model.ModCategory
import com.example.data.model.ModPackEntity
import com.example.ui.MainViewModel
import com.example.ui.components.FileExplorerModal
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LocalAppAccentTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonFuchsia
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.MinecraftLauncher

@Composable
fun ModsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accent = LocalAppAccentTheme.current
    val modPacks by viewModel.filteredModPacks.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedModCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.modSearchQuery.collectAsStateWithLifecycle()

    var selectedPackForDetails by remember { mutableStateOf<ModPackEntity?>(null) }
    var packToDelete by remember { mutableStateOf<ModPackEntity?>(null) }
    var showFileExplorer by remember { mutableStateOf(false) }

    // SAF File Picker for .mcpack and .mcaddon
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.handleImportedFileUri(uri)
            Toast.makeText(context, "Importing Minecraft package...", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(
                title = "Mods & Resource Packs",
                subtitle = "Manage .mcpack, .mcaddon & PvP tweaks",
                action = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // File Explorer Trigger Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(accent.primary, accent.secondary))
                                )
                                .clickable {
                                    showFileExplorer = true
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("import_pack_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.FolderOpen,
                                    contentDescription = "File Explorer",
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "EXPLORER",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                )
                            }
                        }

                        // SAF Quick Launcher Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    filePickerLauncher.launch("*/*")
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .testTag("quick_saf_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FileUpload,
                                contentDescription = "System Picker",
                                tint = accent.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            )
        }

        // Storage Explorer Promo Banner Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showFileExplorer = true },
                borderColor = NeonCyan.copy(alpha = 0.4f),
                backgroundColor = DarkSurfaceElevated
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonCyan.copy(alpha = 0.15f))
                                .border(1.dp, NeonCyan, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Inventory2,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Import .mcpack & .mcaddon",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(NeonFuchsia.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("STORAGE", color = NeonFuchsia, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            Text(
                                text = "Browse Downloads, com.mojang & internal storage",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accent.primary)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "BROWSE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setModSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mod_search_field"),
                placeholder = {
                    Text("Search packs, shaders, textures...", color = TextMuted)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = accent.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setModSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear",
                                tint = TextMuted
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
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

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ModCategory.values()) { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) accent.primary.copy(alpha = 0.2f) else DarkSurfaceElevated
                            )
                            .border(
                                1.dp,
                                if (isSelected) accent.primary else DarkCardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setSelectedModCategory(category) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accent.primary else TextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Packs Count / Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${modPacks.size} Packs Available",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                )
                Text(
                    text = "${modPacks.count { it.isEnabled }} Active",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = StatusOnline
                    )
                )
            }
        }

        // Empty state
        if (modPacks.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Extension,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Packs Found",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Import a .mcpack, .mcaddon or .mcworld file directly from your device storage.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { showFileExplorer = true },
                                colors = ButtonDefaults.buttonColors(containerColor = accent.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.FolderOpen, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Browse Storage", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { filePickerLauncher.launch("*/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                            ) {
                                Icon(Icons.Filled.FileUpload, contentDescription = null, tint = accent.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SAF Picker", color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Mod Pack Items
        items(modPacks, key = { it.id }) { pack ->
            ModPackCard(
                pack = pack,
                onToggle = { viewModel.toggleModPack(pack) },
                onDetails = { selectedPackForDetails = pack },
                onExport = {
                    if (pack.fileUri.isNotBlank()) {
                        MinecraftLauncher.importPack(context, Uri.parse(pack.fileUri))
                    } else {
                        MinecraftLauncher.launchMinecraft(context)
                        Toast.makeText(context, "${pack.title} active in Minecraft!", Toast.LENGTH_SHORT).show()
                    }
                },
                onDelete = { packToDelete = pack }
            )
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }

    // Mod Pack Details Dialog
    if (selectedPackForDetails != null) {
        val pack = selectedPackForDetails!!
        AlertDialog(
            onDismissRequest = { selectedPackForDetails = null },
            containerColor = DarkSurfaceElevated,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pack.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    NeonBadge(text = pack.version, color = accent.primary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = pack.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "PACKAGE METADATA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = accent.secondary
                        )
                    )

                    DetailRow(label = "Category", value = pack.category.displayName)
                    DetailRow(label = "File Name", value = pack.fileName)
                    DetailRow(label = "Size", value = pack.fileSizeFormatted)
                    DetailRow(label = "Author", value = pack.author)
                    DetailRow(label = "Target", value = pack.resolution)

                    if (pack.features.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "KEY FEATURES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = accent.primary
                            )
                        )
                        pack.features.split(",").forEach { feature ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = accent.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = feature.trim(),
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pack.fileUri.isNotBlank()) {
                            MinecraftLauncher.importPack(context, Uri.parse(pack.fileUri))
                        } else {
                            MinecraftLauncher.launchMinecraft(context)
                        }
                        selectedPackForDetails = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
                ) {
                    Text("Launch in Minecraft", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPackForDetails = null }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (packToDelete != null) {
        val pack = packToDelete!!
        AlertDialog(
            onDismissRequest = { packToDelete = null },
            containerColor = DarkSurfaceElevated,
            title = { Text("Delete Pack?", color = TextPrimary) },
            text = {
                Text(
                    "Are you sure you want to remove '${pack.title}' from your client library?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteModPack(pack)
                        packToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { packToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Storage File Explorer Modal for importing .mcpack and .mcaddon
    FileExplorerModal(
        isOpen = showFileExplorer,
        onDismiss = { showFileExplorer = false },
        onImportPack = { entity ->
            viewModel.importModPack(entity)
        },
        onBatchImport = { entities ->
            viewModel.batchImportModPacks(entities)
        },
        onPickViaSystemLauncher = {
            showFileExplorer = false
            filePickerLauncher.launch("*/*")
        }
    )
}

@Composable
fun ModPackCard(
    pack: ModPackEntity,
    onToggle: () -> Unit,
    onDetails: () -> Unit,
    onExport: () -> Unit,
    onDelete: () -> Unit
) {
    val accent = LocalAppAccentTheme.current
    var isExpanded by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (pack.isEnabled) accent.primary.copy(alpha = 0.5f) else DarkCardBorder,
        backgroundColor = if (pack.isEnabled) accent.primary.copy(alpha = 0.08f) else DarkSurfaceElevated
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (pack.isEnabled) accent.primary.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                if (pack.isEnabled) accent.primary else DarkCardBorder,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (pack.category) {
                            ModCategory.TEXTURE_PACK -> Icons.Outlined.Palette
                            ModCategory.ADDON -> Icons.Outlined.Extension
                            ModCategory.SHADER -> Icons.Outlined.WbSunny
                            ModCategory.UI_TWEAK -> Icons.Outlined.Extension
                            ModCategory.UTILITY -> Icons.Outlined.SportsEsports
                            ModCategory.WORLD -> Icons.Outlined.Public
                            else -> Icons.Outlined.Extension
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (pack.isEnabled) accent.primary else TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = pack.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (pack.isEnabled) TextPrimary else TextSecondary
                                ),
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeonBadge(
                                text = pack.category.displayName,
                                color = accent.secondary,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = "${pack.fileSizeFormatted} • ${pack.version}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            )
                        }
                    }
                }

                Switch(
                    checked = pack.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = accent.primary,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkSurfaceElevated
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pack.description,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                maxLines = if (isExpanded) 10 else 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .clickable { onDetails() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Details",
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Details",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accent.primary.copy(alpha = 0.15f))
                            .clickable { onExport() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Launch,
                                contentDescription = "Launch",
                                tint = accent.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Export to MC",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = accent.primary
                                )
                            )
                        }
                    }
                }

                if (!pack.isBuiltIn) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
        Text(text = value, style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary))
    }
}
