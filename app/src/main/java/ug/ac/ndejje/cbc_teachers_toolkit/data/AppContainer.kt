package ug.ac.ndejje.cbc_teachers_toolkit.data

import android.content.Context
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.AppDatabase

class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    val topicRepository: TopicRepository = TopicRepository(database.topicDao())
    val authRepository: AuthRepository = AuthRepository(database.userDao())
}
