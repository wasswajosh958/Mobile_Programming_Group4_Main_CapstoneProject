package ug.ac.ndejje.cbc_teachers_toolkit

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun appViewModel(): SubjectViewModel {
    val app = LocalContext.current.applicationContext as CbcToolkitApplication
    return viewModel(factory = SubjectViewModel.Factory(app.container.topicRepository))
}
