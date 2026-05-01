package ug.ac.ndejje.cbc_teachers_toolkit.data

import android.content.Context
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.AppDatabase
import ug.ac.ndejje.cbc_teachers_toolkit.data.remote.FirebaseSyncManager

class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val firebaseSyncManager = FirebaseSyncManager()
    val topicRepository: TopicRepository = TopicRepository(
        context,
        database.topicDao(),
        database.userDao(),
        database.adminResourceDao(),
        firebaseSyncManager
    )
    val authRepository: AuthRepository = AuthRepository(database.userDao())
}
