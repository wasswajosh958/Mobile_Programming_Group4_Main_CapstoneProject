package ug.ac.ndejje.cbc_teachers_toolkit.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val username: String,
    val password: String,
    val isLoggedIn: Boolean = false,
    val isAdmin: Boolean = false,
    val githubToken: String? = null,
    val interestedSubjects: String? = null // Comma separated list of subjects
)
