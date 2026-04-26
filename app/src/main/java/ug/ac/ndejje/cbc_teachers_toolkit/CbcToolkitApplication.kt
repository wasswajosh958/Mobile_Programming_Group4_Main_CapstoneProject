package ug.ac.ndejje.cbc_teachers_toolkit

import android.app.Application
import ug.ac.ndejje.cbc_teachers_toolkit.data.AppContainer

class CbcToolkitApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
