package ug.ac.ndejje.cbc_teachers_toolkit.data.remote

import ug.ac.ndejje.cbc_teachers_toolkit.data.local.TeachingResourceEntity

/**
 * DEPRECATED: We are moving to GitHub JSON sync to keep the app 100% free and offline-first.
 * Keeping a shell class to avoid breaking repository injection until fully removed.
 */
class FirebaseSyncManager {
    suspend fun fetchResourcesFromFirestore(): List<TeachingResourceEntity> {
        return emptyList()
    }
}
