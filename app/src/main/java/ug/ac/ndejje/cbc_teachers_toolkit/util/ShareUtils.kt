package ug.ac.ndejje.cbc_teachers_toolkit.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import ug.ac.ndejje.cbc_teachers_toolkit.data.local.SchemeOfWorkEntity
import ug.ac.ndejje.cbc_teachers_toolkit.domain.Topic
import java.io.File
import java.io.FileOutputStream

fun shareScheme(context: Context, scheme: SchemeOfWorkEntity) {
    try {
        val pdfDocument = PdfDocument()
        // Landscape orientation for scheme of work
        val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create() // A4 Landscape
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        
        val titlePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 16f
            color = Color.BLACK
        }
        val headerPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 10f
            color = Color.BLACK
        }
        val cellPaint = Paint().apply {
            textSize = 9f
            color = Color.BLACK
        }
        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        var y = 40f
        val centerX = 842f / 2
        
        // Header Information
        canvas.drawText("REPUBLIC OF UGANDA", centerX - titlePaint.measureText("REPUBLIC OF UGANDA")/2, y, titlePaint)
        y += 20f
        canvas.drawText("SCHEME OF WORK", centerX - titlePaint.measureText("SCHEME OF WORK")/2, y, titlePaint)
        y += 30f

        // Top info row
        val margin = 40f
        val tableWidth = 842f - (margin * 2)
        
        canvas.drawText("SCHOOL: ${scheme.schoolName.uppercase()}", margin, y, headerPaint)
        canvas.drawText("TEACHER: ${scheme.teacherName.uppercase()}", margin + 300f, y, headerPaint)
        canvas.drawText("DATE: ${scheme.date}", margin + 600f, y, headerPaint)
        y += 15f
        canvas.drawText("SUBJECT: ${scheme.subject}", margin, y, headerPaint)
        canvas.drawText("CLASS: ${scheme.classLevel}", margin + 300f, y, headerPaint)
        canvas.drawText("TERM: ${scheme.term}  WEEK: ${scheme.week}", margin + 600f, y, headerPaint)
        y += 30f

        // Table Header
        val cols = listOf(
            "TOPIC / SUB-TOPIC" to 0.15f,
            "COMPETENCY" to 0.15f,
            "LEARNING OBJECTIVES" to 0.20f,
            "LEARNING ACTIVITIES" to 0.20f,
            "RESOURCES" to 0.15f,
            "ASSESSMENT" to 0.15f
        )
        
        var currentX = margin
        for ((text, weight) in cols) {
            val colWidth = tableWidth * weight
            canvas.drawRect(currentX, y, currentX + colWidth, y + 30f, linePaint)
            
            // Center text in header cell
            val textX = currentX + (colWidth / 2) - (headerPaint.measureText(text) / 2)
            canvas.drawText(text, textX, y + 20f, headerPaint)
            currentX += colWidth
        }
        
        y += 30f
        
        // Table Content
        val content = listOf(
            scheme.topicTitle,
            scheme.competency,
            scheme.objectives,
            scheme.activities,
            scheme.resources,
            scheme.assessment
        )
        
        currentX = margin
        val rowHeight = 350f // Large fixed height for now, or dynamic
        
        for (i in content.indices) {
            val colWidth = tableWidth * cols[i].second
            canvas.drawRect(currentX, y, currentX + colWidth, y + rowHeight, linePaint)
            
            // Wrap text in cell
            drawTextInCell(canvas, content[i], currentX + 5f, y + 15f, colWidth - 10f, cellPaint)
            
            currentX += colWidth
        }

        pdfDocument.finishPage(page)

        val fileName = "Scheme_${scheme.topicTitle.replace(" ", "_")}.pdf"
        val file = File(context.cacheDir, fileName)
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        shareFile(context, file, "application/pdf", "Share Scheme of Work")
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun openScheme(context: Context, scheme: SchemeOfWorkEntity) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        
        val titlePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 16f
            color = Color.BLACK
        }
        val headerPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 10f
            color = Color.BLACK
        }
        val cellPaint = Paint().apply {
            textSize = 9f
            color = Color.BLACK
        }
        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        var y = 40f
        val centerX = 842f / 2
        
        canvas.drawText("REPUBLIC OF UGANDA", centerX - titlePaint.measureText("REPUBLIC OF UGANDA")/2, y, titlePaint)
        y += 20f
        canvas.drawText("SCHEME OF WORK", centerX - titlePaint.measureText("SCHEME OF WORK")/2, y, titlePaint)
        y += 30f

        val margin = 40f
        val tableWidth = 842f - (margin * 2)
        
        canvas.drawText("SCHOOL: ${scheme.schoolName.uppercase()}", margin, y, headerPaint)
        canvas.drawText("TEACHER: ${scheme.teacherName.uppercase()}", margin + 300f, y, headerPaint)
        canvas.drawText("DATE: ${scheme.date}", margin + 600f, y, headerPaint)
        y += 15f
        canvas.drawText("SUBJECT: ${scheme.subject}", margin, y, headerPaint)
        canvas.drawText("CLASS: ${scheme.classLevel}", margin + 300f, y, headerPaint)
        canvas.drawText("TERM: ${scheme.term}  WEEK: ${scheme.week}", margin + 600f, y, headerPaint)
        y += 30f

        val cols = listOf(
            "TOPIC / SUB-TOPIC" to 0.15f,
            "COMPETENCY" to 0.15f,
            "LEARNING OBJECTIVES" to 0.20f,
            "LEARNING ACTIVITIES" to 0.20f,
            "RESOURCES" to 0.15f,
            "ASSESSMENT" to 0.15f
        )
        
        var currentX = margin
        for ((text, weight) in cols) {
            val colWidth = tableWidth * weight
            canvas.drawRect(currentX, y, currentX + colWidth, y + 30f, linePaint)
            val textX = currentX + (colWidth / 2) - (headerPaint.measureText(text) / 2)
            canvas.drawText(text, textX, y + 20f, headerPaint)
            currentX += colWidth
        }
        
        y += 30f
        
        val content = listOf(
            scheme.topicTitle,
            scheme.competency,
            scheme.objectives,
            scheme.activities,
            scheme.resources,
            scheme.assessment
        )
        
        currentX = margin
        val rowHeight = 350f
        
        for (i in content.indices) {
            val colWidth = tableWidth * cols[i].second
            canvas.drawRect(currentX, y, currentX + colWidth, y + rowHeight, linePaint)
            drawTextInCell(canvas, content[i], currentX + 5f, y + 15f, colWidth - 10f, cellPaint)
            currentX += colWidth
        }

        pdfDocument.finishPage(page)

        val fileName = "View_Scheme_${scheme.topicTitle.replace(" ", "_")}.pdf"
        val file = File(context.cacheDir, fileName)
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        viewFile(context, file, "application/pdf")
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun drawTextInCell(canvas: Canvas, text: String, x: Float, y: Float, maxWidth: Float, paint: Paint) {
    val words = text.split("\\s+".toRegex())
    var line = ""
    var currentY = y
    
    for (word in words) {
        if (paint.measureText("$line $word") < maxWidth) {
            line += if (line.isEmpty()) word else " $word"
        } else {
            canvas.drawText(line, x, currentY, paint)
            currentY += paint.textSize + 5f
            line = word
        }
    }
    canvas.drawText(line, x, currentY, paint)
}

fun shareNotes(context: Context, topic: Topic, note: String) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val titlePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 18f
        }
        val headerPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 14f
        }
        val textPaint = Paint().apply {
            textSize = 12f
        }

        var y = 50f
        canvas.drawText("CBC TEACHERS' TOOLKIT - LESSON NOTES", 120f, y, titlePaint)
        y += 40f
        
        canvas.drawText("Topic: ${topic.title}", 50f, y, headerPaint)
        y += 20f
        canvas.drawText("Subject: ${topic.subject} | Class: ${topic.classLevel}", 50f, y, textPaint)
        y += 30f
        
        canvas.drawLine(50f, y, 545f, y, Paint())
        y += 30f
        
        canvas.drawText("MY OBSERVATIONS & NOTES:", 50f, y, headerPaint)
        y += 25f

        val words = note.split("\\s+".toRegex())
        var line = ""
        for (word in words) {
            if (textPaint.measureText("$line $word") < 495f) {
                line += "$word "
            } else {
                canvas.drawText(line, 50f, y, textPaint)
                y += 20f
                line = "$word "
            }
            if (y > 800f) break
        }
        canvas.drawText(line, 50f, y, textPaint)

        pdfDocument.finishPage(page)

        val fileName = "Notes_${topic.title.replace(" ", "_")}.pdf"
        val file = File(context.cacheDir, fileName)
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        shareFile(context, file, "application/pdf", "Share Lesson Notes")
    } catch (e: Exception) {
        Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun openNotesAsPdf(context: Context, topic: Topic, note: String) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val titlePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 18f
        }
        val headerPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 14f
        }
        val textPaint = Paint().apply {
            textSize = 12f
        }

        var y = 50f
        canvas.drawText("CBC TEACHERS' TOOLKIT - LESSON NOTES", 120f, y, titlePaint)
        y += 40f
        
        canvas.drawText("Topic: ${topic.title}", 50f, y, headerPaint)
        y += 20f
        canvas.drawText("Subject: ${topic.subject} | Class: ${topic.classLevel}", 50f, y, textPaint)
        y += 30f
        
        canvas.drawLine(50f, y, 545f, y, Paint())
        y += 30f
        
        canvas.drawText("MY OBSERVATIONS & NOTES:", 50f, y, headerPaint)
        y += 25f

        val words = note.split("\\s+".toRegex())
        var line = ""
        for (word in words) {
            if (textPaint.measureText("$line $word") < 495f) {
                line += "$word "
            } else {
                canvas.drawText(line, 50f, y, textPaint)
                y += 20f
                line = "$word "
            }
            if (y > 800f) break
        }
        canvas.drawText(line, 50f, y, textPaint)

        pdfDocument.finishPage(page)

        val fileName = "View_Notes_${topic.title.replace(" ", "_")}.pdf"
        val file = File(context.cacheDir, fileName)
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        viewFile(context, file, "application/pdf")
    } catch (e: Exception) {
        Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
    val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, contentUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, chooserTitle))
}

private fun viewFile(context: Context, file: File, mimeType: String) {
    val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val viewIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(contentUri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(viewIntent)
}
