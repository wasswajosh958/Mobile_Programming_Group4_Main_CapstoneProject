package ug.ac.ndejje.cbc_teachers_toolkit.data

import kotlinx.coroutines.flow.Flow
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.UserDao
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.UserEntity

class AuthRepository(
    private val userDao: UserDao
) {
    fun observeCurrentUser(): Flow<UserEntity?> = userDao.observeCurrentUser()

    suspend fun ensureDefaultUser() {
        val existing = userDao.findByUsername(DEFAULT_USERNAME)
        if (existing == null) {
            userDao.insertUser(
                UserEntity(
                    fullName = "Default Teacher",
                    username = DEFAULT_USERNAME,
                    password = DEFAULT_PASSWORD
                )
            )
        }
        val admin = userDao.findByUsername(ADMIN_USERNAME)
        if (admin == null) {
            userDao.insertUser(
                UserEntity(
                    fullName = "Administrator",
                    username = ADMIN_USERNAME,
                    password = ADMIN_PASSWORD,
                    isAdmin = true
                )
            )
        }
    }

    suspend fun register(fullName: String, username: String, password: String, interestedSubjects: List<String>): Result<Unit> {
        val existing = userDao.findByUsername(username.trim())
        if (existing != null) return Result.failure(IllegalArgumentException("Username already exists"))

        val userId = userDao.insertUser(
            UserEntity(
                fullName = fullName.trim(),
                username = username.trim(),
                password = password,
                interestedSubjects = interestedSubjects.joinToString(",")
            )
        )
        userDao.loginById(userId)
        return Result.success(Unit)
    }

    suspend fun login(username: String, password: String): Result<Unit> {
        val user = userDao.findByCredentials(username.trim(), password)
            ?: return Result.failure(IllegalArgumentException("Invalid username or password"))
        userDao.loginById(user.id)
        return Result.success(Unit)
    }

    suspend fun logout() {
        userDao.logoutAll()
    }

    suspend fun updateGithubToken(token: String) {
        userDao.updateCurrentToken(token)
    }

    companion object {
        private const val DEFAULT_USERNAME = "teacher"
        private const val DEFAULT_PASSWORD = "1234"
        private const val ADMIN_USERNAME = "admin"
        private const val ADMIN_PASSWORD = "12345"
    }
}
