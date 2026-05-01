package ug.ac.ndejje.cbc_teachers_toolkit.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import ug.ac.ndejje.cbc_teachers_toolkit.CbcToolkitApplication

@Composable
fun appViewModel(): SubjectViewModel {
    val app = LocalContext.current.applicationContext as CbcToolkitApplication
    return viewModel(factory = SubjectViewModel.Factory(app.container.topicRepository, app.container.authRepository))
}

@Composable
fun authViewModel(): AuthViewModel {
    val app = LocalContext.current.applicationContext as CbcToolkitApplication
    return viewModel(factory = AuthViewModel.Factory(app.container.authRepository))
}
