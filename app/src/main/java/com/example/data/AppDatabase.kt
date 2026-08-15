package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ModPackDao
import com.example.data.dao.ServerDao
import com.example.data.model.ModCategory
import com.example.data.model.ModPackEntity
import com.example.data.model.ServerEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromModCategory(value: ModCategory): String = value.name

    @TypeConverter
    fun toModCategory(value: String): ModCategory = try {
        ModCategory.valueOf(value)
    } catch (e: Exception) {
        ModCategory.ALL
    }
}

@Database(
    entities = [ServerEntity::class, ModPackEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
    abstract fun modPackDao(): ModPackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mcpe_client_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).seedInitialData()
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun seedInitialData() {
        val initialServers = listOf(
            ServerEntity(
                name = "The Hive Bedrock",
                ip = "geo.hivebedrock.network",
                port = 19132,
                description = "World's most popular Bedrock network with Treasure Wars, SkyWars, Hide and Seek, and Murder Mystery.",
                isFeatured = true,
                isFavorite = true,
                bannerColorHex = "#F59E0B",
                pingMs = 28,
                onlinePlayers = 24850,
                maxPlayers = 35000,
                motd = "§6The Hive §8» §eSeason 12 LIVE! §a[BedWars & SkyWars]",
                gameModes = "Treasure Wars, Survival Games, Hide & Seek, SkyWars"
            ),
            ServerEntity(
                name = "NetherGames Network",
                ip = "play.nethergames.org",
                port = 19132,
                description = "The ultimate competitive PvP Bedrock server with Duels, BedWars, Factions and Conquest.",
                isFeatured = true,
                isFavorite = true,
                bannerColorHex = "#A855F7",
                pingMs = 34,
                onlinePlayers = 8240,
                maxPlayers = 15000,
                motd = "§5NetherGames §d[COMPETITIVE PVP] §bRanked Duels & BedWars",
                gameModes = "Ranked Duels, BedWars, SkyWars, Bridge, Conquest"
            ),
            ServerEntity(
                name = "CubeCraft Games",
                ip = "play.cubecraft.net",
                port = 19132,
                description = "Official partner network offering EggWars, Lucky Islands, BlockWars, and Tower Defence.",
                isFeatured = true,
                isFavorite = false,
                bannerColorHex = "#00F0FF",
                pingMs = 45,
                onlinePlayers = 14200,
                maxPlayers = 25000,
                motd = "§bCubeCraft Games §7- §eEggWars & Skyblock Updates!",
                gameModes = "EggWars, Lucky Islands, Tower Defence, Skyblock"
            ),
            ServerEntity(
                name = "Lifeboat Network",
                ip = "play.lbsg.net",
                port = 19132,
                description = "Classic Minecraft Bedrock server featuring Survival Games, Prison, Skyblock, and Creative.",
                isFeatured = true,
                isFavorite = false,
                bannerColorHex = "#10B981",
                pingMs = 52,
                onlinePlayers = 9100,
                maxPlayers = 20000,
                motd = "§aLifeboat Network §7- §fMini-games and Survival!",
                gameModes = "Survival, Skyblock, Prison, Survival Games"
            ),
            ServerEntity(
                name = "Galaxite",
                ip = "play.galaxite.net",
                port = 19132,
                description = "Intergalactic themed custom mini-games with Chronos, Hyper Racers, Core Wars, and Alien Invasion.",
                isFeatured = true,
                isFavorite = false,
                bannerColorHex = "#EC4899",
                pingMs = 60,
                onlinePlayers = 3850,
                maxPlayers = 10000,
                motd = "§dGalaxite §f- §bSci-Fi Arcade Games & Custom Cosmetics",
                gameModes = "Chronos, Rush, Core Wars, Prop Hunt"
            ),
            ServerEntity(
                name = "Mineville",
                ip = "play.inpvp.net",
                port = 19132,
                description = "Roleplay and city simulation server with City Living, High School RP, Prison, and Skyblock.",
                isFeatured = true,
                isFavorite = false,
                bannerColorHex = "#3B82F6",
                pingMs = 68,
                onlinePlayers = 4120,
                maxPlayers = 12000,
                motd = "§9Mineville §7- §eAnime City, High School & Kingdom RP",
                gameModes = "City Living, High School RP, Skyblock, Prison"
            )
        )
        serverDao().insertAll(initialServers)

        val initialModPacks = listOf(
            ModPackEntity(
                title = "Neon PvP Boost 32x",
                category = ModCategory.TEXTURE_PACK,
                fileName = "NeonPvP_32x.mcpack",
                fileSizeFormatted = "18.4 MB",
                version = "v3.2",
                author = "MCPE Studio",
                description = "Short swords, clean neon highlighted ores, clear water, low fire, and transparent particle effects for maximum combat visibility.",
                isEnabled = true,
                isBuiltIn = true,
                features = "Short Swords, Clean Ores, Low Fire, 32x Textures, Transparent GUI",
                resolution = "32x"
            ),
            ModPackEntity(
                title = "Esthetic Vibrant Shaders",
                category = ModCategory.SHADER,
                fileName = "Vibrant_Bedrock_Shaders.mcpack",
                fileSizeFormatted = "4.2 MB",
                version = "v2.0",
                author = "CyberCraft",
                description = "Lightweight warm sunlight, waving grass & foliage, realistic dynamic water reflections without frame drops.",
                isEnabled = true,
                isBuiltIn = true,
                features = "Dynamic Sunbeams, Waving Foliage, Clear Water, Zero Lag",
                resolution = "Shader"
            ),
            ModPackEntity(
                title = "MCPE Client Dark HUD UI",
                category = ModCategory.UI_TWEAK,
                fileName = "Client_Dark_UI.mcpack",
                fileSizeFormatted = "8.1 MB",
                version = "v4.1",
                author = "MCPE Client Devs",
                description = "Sleek dark translucent inventory, RGB health bars, quick drop button, custom hotbar slots, and armor durability display.",
                isEnabled = true,
                isBuiltIn = true,
                features = "Translucent Inventory, Quick Drop, Durability Info, Dark Hotbar",
                resolution = "UI"
            ),
            ModPackEntity(
                title = "Fullbright & Night Vision",
                category = ModCategory.UTILITY,
                fileName = "Fullbright_Gamma_Max.mcpack",
                fileSizeFormatted = "1.1 MB",
                version = "v1.8",
                author = "PvP Elite",
                description = "Maximum ambient illumination in caves, Nether, and underwater without placing torches.",
                isEnabled = true,
                isBuiltIn = true,
                features = "Infinite Cave Vision, Nether Visibility, Clear Water",
                resolution = "Utility"
            ),
            ModPackEntity(
                title = "Competitive PvP Arena & Trainer",
                category = ModCategory.WORLD,
                fileName = "PvP_Master_Arena.mcworld",
                fileSizeFormatted = "32.6 MB",
                version = "v1.0",
                author = "Bedrock Arena Team",
                description = "Offline warm-up world with bot dueling, bridge training, CPS click target course, and MLG water bucket challenges.",
                isEnabled = false,
                isBuiltIn = true,
                features = "Bot Duels, Speed Bridge Course, Parkour, MLG Bucket Trainer",
                resolution = "World"
            )
        )
        modPackDao().insertAll(initialModPacks)
    }
}
