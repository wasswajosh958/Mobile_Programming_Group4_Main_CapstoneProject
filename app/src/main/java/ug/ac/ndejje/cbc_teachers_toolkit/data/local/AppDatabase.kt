package ug.ac.ndejje.cbc_teachers_toolkit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TopicEntity::class,
        FavoriteEntity::class,
        NoteEntity::class,
        TeachingResourceEntity::class,
        SchemeOfWorkEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cbc_teachers_toolkit.db"
                ).fallbackToDestructiveMigration(false).build().also { INSTANCE = it }
            }
        }
    }
}
