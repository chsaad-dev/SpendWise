package com.example.spendwise.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.spendwise.data.CategoryTotal
import com.example.spendwise.data.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportHelper {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 50f

    suspend fun generateMonthlyReport(
        context: Context,
        monthYearTitle: String,
        expenses: List<Expense>,
        categoryTotals: List<CategoryTotal>,
        totalIncome: Double,
        totalExpense: Double
    ): File = withContext(Dispatchers.IO) {

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.parseColor("#006B75")
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val headerPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val textPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
        }

        val valPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        var yPos = MARGIN + 20f

        // Draw Title
        canvas.drawText("SpendWise Monthly Report", MARGIN, yPos, titlePaint)
        yPos += 30f
        canvas.drawText(monthYearTitle, MARGIN, yPos, headerPaint)
        yPos += 40f

        // Draw Summary
        canvas.drawText("Summary", MARGIN, yPos, headerPaint)
        yPos += 20f
        
        canvas.drawText("Total Income:", MARGIN, yPos, textPaint)
        canvas.drawText(currencyFormat.format(totalIncome), PAGE_WIDTH - MARGIN, yPos, valPaint)
        yPos += 20f

        canvas.drawText("Total Expense:", MARGIN, yPos, textPaint)
        valPaint.color = Color.parseColor("#EF5350") // Red for expense
        canvas.drawText(currencyFormat.format(totalExpense), PAGE_WIDTH - MARGIN, yPos, valPaint)
        yPos += 20f

        val netBalance = totalIncome - totalExpense
        canvas.drawText("Net Balance:", MARGIN, yPos, textPaint)
        valPaint.color = if (netBalance >= 0) Color.parseColor("#4CAF50") else Color.parseColor("#EF5350")
        canvas.drawText(currencyFormat.format(netBalance), PAGE_WIDTH - MARGIN, yPos, valPaint)
        yPos += 40f

        valPaint.color = Color.BLACK // Reset

        // Draw Category Breakdown
        if (categoryTotals.isNotEmpty()) {
            canvas.drawText("Category Breakdown (Expenses)", MARGIN, yPos, headerPaint)
            yPos += 20f

            categoryTotals.forEach { cat ->
                canvas.drawText(cat.category, MARGIN + 10f, yPos, textPaint)
                canvas.drawText(currencyFormat.format(cat.total), PAGE_WIDTH - MARGIN, yPos, valPaint)
                yPos += 20f
                if (yPos > PAGE_HEIGHT - MARGIN - 30f) {
                    pdfDocument.finishPage(page)
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = MARGIN + 20f
                }
            }
            yPos += 20f
        }

        // Draw Transactions
        if (expenses.isNotEmpty()) {
            canvas.drawText("Transactions", MARGIN, yPos, headerPaint)
            yPos += 20f

            // Table Header
            canvas.drawText("Date", MARGIN, yPos, textPaint)
            canvas.drawText("Category", MARGIN + 80f, yPos, textPaint)
            // Limit note width, we'll draw amount at right edge
            canvas.drawText("Note", MARGIN + 180f, yPos, textPaint)
            canvas.drawText("Amount", PAGE_WIDTH - MARGIN, yPos, Paint(textPaint).apply { textAlign = Paint.Align.RIGHT })
            yPos += 8f
            canvas.drawLine(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos, Paint().apply { color = Color.LTGRAY; strokeWidth = 1f })
            yPos += 16f

            expenses.forEach { exp ->
                if (yPos > PAGE_HEIGHT - MARGIN - 20f) {
                    pdfDocument.finishPage(page)
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = MARGIN + 20f
                }

                val dateStr = dateFormat.format(Date(exp.date))
                canvas.drawText(dateStr, MARGIN, yPos, textPaint)
                
                val catStr = exp.category.take(12) + if (exp.category.length > 12) "..." else ""
                canvas.drawText(catStr, MARGIN + 80f, yPos, textPaint)
                
                val noteStr = exp.note.replace("\n", " ").take(30) + if (exp.note.length > 30) "..." else ""
                canvas.drawText(noteStr, MARGIN + 180f, yPos, textPaint)

                val amtStr = (if (exp.isIncome) "+" else "-") + currencyFormat.format(exp.amount)
                val amtPaint = Paint(valPaint).apply { 
                    color = if (exp.isIncome) Color.parseColor("#4CAF50") else Color.parseColor("#EF5350")
                    textSize = 10f
                }
                canvas.drawText(amtStr, PAGE_WIDTH - MARGIN, yPos, amtPaint)

                yPos += 20f
            }
        }

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "SpendWise_Report.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        file
    }
}
