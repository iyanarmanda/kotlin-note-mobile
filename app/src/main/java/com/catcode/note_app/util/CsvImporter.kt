package com.catcode.note_app.util

import com.catcode.note_app.data.entity.NoteEntity
import java.io.InputStream

object CsvImporter {

  fun readNotesFromCsv(inputStream: InputStream): List<NoteEntity> {
    val notes = mutableListOf<NoteEntity>()

    inputStream.bufferedReader().use { reader ->
      var line = reader.readLine()
      
      while (true) {
        line = reader.readLine() ?: break
        if (line.isBlank()) continue

        try {
          val note = parseCsvLine(line)
          if (note != null) {
            notes.add(note)
          }
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }

    return notes
  }

  private fun parseCsvLine(line: String): NoteEntity? {
    val fields = mutableListOf<String>()
    var currentField = StringBuilder()
    var insideQuotes = false
    var i = 0

    while (i < line.length) {
      val char = line[i]

      when {
        char == '"' -> {
          insideQuotes = !insideQuotes
        }
        char == ',' && !insideQuotes -> {
          fields.add(currentField.toString().trim().trim('"'))
          currentField = StringBuilder()
        }
        else -> {
          currentField.append(char)
        }
      }

      i++
    }

    fields.add(currentField.toString().trim().trim('"'))

    if (fields.size < 6) return null

    return try {
      NoteEntity(
        id = 0, // Let the database generate new ID
        name = fields[1].trim(),
        date = fields[2].trim(),
        address = fields[3].trim(),
        price = fields[4].trim().toLongOrNull() ?: 0L,
        status = fields[5].trim()
      )
    } catch (e: Exception) {
      null
    }
  }
}
