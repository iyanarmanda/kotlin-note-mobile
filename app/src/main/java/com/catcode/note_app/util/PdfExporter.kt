package com.catcode.note_app.util

import com.catcode.note_app.data.entity.NoteEntity
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PdfExporter {

  fun writeNotesToPdf(
    notes: List<NoteEntity>,
    outputStream: OutputStream
  ) {
    val writer = PdfWriter(outputStream)
    val pdfDocument = PdfDocument(writer)
    val document = Document(pdfDocument)

    // Title
    val title = Paragraph("Notes Report")
      .setTextAlignment(TextAlignment.CENTER)
      .setFontSize(18f)
      .setBold()
    document.add(title)

    // Export date
    val exportDate = LocalDateTime.now()
      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val dateInfo = Paragraph("Exported: $exportDate")
      .setTextAlignment(TextAlignment.CENTER)
      .setFontSize(10f)
    document.add(dateInfo)

    document.add(Paragraph("\n"))

    // Table
    val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f, 2f, 2.5f, 1.5f, 1.5f)))
      .setWidth(UnitValue.createPercentValue(100f))

    // Header
    val headers = listOf("ID", "Name", "Date", "Address", "Price", "Status")
    headers.forEach { header ->
      val cell = Cell()
        .add(Paragraph(header).setBold())
        .setTextAlignment(TextAlignment.CENTER)
      table.addHeaderCell(cell)
    }

    // Rows
    notes.forEach { note ->
      table.addCell(Cell().add(Paragraph(note.id.toString())).setTextAlignment(TextAlignment.CENTER))
      table.addCell(Cell().add(Paragraph(note.name)))
      table.addCell(Cell().add(Paragraph(note.date)).setTextAlignment(TextAlignment.CENTER))
      table.addCell(Cell().add(Paragraph(note.address)))
      table.addCell(Cell().add(Paragraph(formatPrice(note.price))).setTextAlignment(TextAlignment.RIGHT))
      table.addCell(Cell().add(Paragraph(note.status)))
    }

    document.add(table)

    // Footer with summary
    document.add(Paragraph("\n"))
    val summary = Paragraph("Total Notes: ${notes.size}")
      .setTextAlignment(TextAlignment.RIGHT)
      .setFontSize(10f)
    document.add(summary)

    document.close()
  }

  private fun formatPrice(price: Long): String {
    return if (price == 0L) "-" else "Rp ${String.format("%,d", price)}"
  }
}
