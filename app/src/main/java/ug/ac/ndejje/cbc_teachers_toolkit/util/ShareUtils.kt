package ug.ac.ndejje.cbc_teachers_toolkit.util

import android.content.Context
import android.content.Intent
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.SchemeOfWorkEntity

fun shareScheme(context: Context, scheme: SchemeOfWorkEntity) {
    val text = buildString {
        appendLine("CBC Teachers' Toolkit - Scheme of Work")
        appendLine("Teacher: ${scheme.teacherName}")
        appendLine("Subject: ${scheme.subject}")
        appendLine("Class: ${scheme.classLevel}")
        appendLine("Term/Week: ${scheme.term} / Week ${scheme.week}")
        appendLine("Topic: ${scheme.topicTitle}")
        appendLine("Objectives: ${scheme.objectives}")
        appendLine("Activities: ${scheme.activities}")
        appendLine("Resources: ${scheme.resources}")
        appendLine("Assessment: ${scheme.assessment}")
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Scheme of Work - ${scheme.subject}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share scheme"))
}
