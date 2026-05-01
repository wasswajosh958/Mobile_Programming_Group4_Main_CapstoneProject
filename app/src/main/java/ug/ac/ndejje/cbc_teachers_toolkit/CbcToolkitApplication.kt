package ug.ac.ndejje.cbc_teachers_toolkit

import android.app.Application
import androidx.work.Configuration
import ug.ac.ndejje.cbc_teachers_toolkit.data.AppContainer

class CbcToolkitApplication : Application(), Configuration.Provider {
    // This container holds all the data and repositories for the app
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        // Initialize the container when the app starts
        container = AppContainer(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
