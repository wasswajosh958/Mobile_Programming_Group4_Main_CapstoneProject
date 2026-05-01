package ug.ac.ndejje.cbc_teachers_toolkit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// This is our main database for the app using Room
@Database(
    entities = [
        TopicEntity::class,
        FavoriteEntity::class,
        NoteEntity::class,
        TeachingResourceEntity::class,
        SchemeOfWorkEntity::class,
        UserEntity::class,
        AdminResourceEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun userDao(): UserDao
    abstract fun adminResourceDao(): AdminResourceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cbc_teachers_toolkit.db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
