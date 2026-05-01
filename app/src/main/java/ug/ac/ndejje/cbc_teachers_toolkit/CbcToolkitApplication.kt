package ug.ac.ndejje.cbc_teachers_toolkit

import android.app.Application
import androidx.work.Configuration
import ug.ac.ndejje.cbc_teachers_toolkit.data.AppContainer

class CbcToolkitApplication : Application(), Configuration.Provider {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
