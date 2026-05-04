package com.catcode.note_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.drawerlayout.widget.DrawerLayout
import kotlinx.coroutines.launch
import android.widget.LinearLayout
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.EditText
import android.widget.ImageView
import android.view.MotionEvent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.catcode.note_app.R
import com.catcode.note_app.ui.NoteAdapter
import com.catcode.note_app.ui.IndexAdapter
import com.catcode.note_app.ui.TwoDScrollView
import com.catcode.note_app.ui.NoteViewModel
import com.catcode.note_app.ui.NoteViewModelFactory
import com.catcode.note_app.ui.dialog.MoreActionDialog
import com.catcode.note_app.ui.dialog.AddNoteDialog
import com.catcode.note_app.ui.dialog.EditNoteDialog
import com.catcode.note_app.ui.dialog.ConfirmDeleteDialog
import com.catcode.note_app.ui.dialog.ConfirmResetDialog
import com.catcode.note_app.data.db.AppDatabase
import com.catcode.note_app.data.repository.NoteRepository
import com.catcode.note_app.data.entity.NoteEntity

class MainActivity : AppCompatActivity() {

  private lateinit var viewModel: NoteViewModel
  private lateinit var repository: NoteRepository
  private lateinit var noteAdapter: NoteAdapter
  private lateinit var indexAdapter: IndexAdapter

  private val exportCsvLauncher =
    registerForActivityResult(
      androidx.activity.result.contract.ActivityResultContracts.CreateDocument("text/csv")
  ) { uri ->
    if (uri != null) {
      exportCsv(uri)
    }
  }

  private val importCsvLauncher =
    registerForActivityResult(
      androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
    ) { uri ->
      if (uri != null) {
        importCsv(uri)
      }
    }

  private val exportPdfLauncher =
    registerForActivityResult(
      androidx.activity.result.contract.ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
      if (uri != null) {
        exportPdf(uri)
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val mainRv = findViewById<RecyclerView>(R.id.recyclerView)
    val indexRv = findViewById<RecyclerView>(R.id.indexRecycler)

    val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

    val tableScroll = findViewById<TwoDScrollView>(R.id.tableScroll)
    val headerScroll = findViewById<HorizontalScrollView>(R.id.headerScroll)

    val database = AppDatabase.getInstance(this)
    repository = NoteRepository(database.noteDao())

    val factory = NoteViewModelFactory(repository)
    viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

    val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
    val moreActionMenu = findViewById<ImageView>(R.id.moreActionMenu)

    val layoutSortDate = findViewById<LinearLayout>(R.id.layoutSortDate)
    val iconSortDate = findViewById<ImageView>(R.id.iconSortDate)

    noteAdapter = NoteAdapter(
      mutableListOf(),
      onRowSelected = {
        position -> indexAdapter.setSelectedPosition(position)
      },
      onEdit = {
        note -> EditNoteDialog(
          context = this,
          note = note,
          onSubmit = {
            updated -> viewModel.updateNote(updated)
          }
        ).show()
      },
      onDelete = {
        note -> ConfirmDeleteDialog.show(
          context = this,
          onConfirm = {
            viewModel.deleteNote(note)
          }
        )
      }
    )

    indexAdapter = IndexAdapter(
      0,
      onIndexSelected = {
        position -> noteAdapter.setSelectedPosition(position)
      },
      onEdit ={
        position -> val note = viewModel.notes.value.getOrNull(position)
          ?: return@IndexAdapter

        EditNoteDialog(
          context = this,
          note = note,
          onSubmit = {
            updated -> viewModel.updateNote(updated)
          }
        ).show()
      },
      onDelete = {
        position -> val note = viewModel.notes.value.getOrNull(position)
          ?: return@IndexAdapter

        ConfirmDeleteDialog.show(
          context = this,
          onConfirm = {
            viewModel.deleteNote(note)
          }
        )
      }
    )

    mainRv.layoutManager = LinearLayoutManager(this)
    indexRv.layoutManager = LinearLayoutManager(this)

    mainRv.adapter = noteAdapter
    indexRv.adapter = indexAdapter

    observeNotes()
    viewModel.loadNotes()

    if (viewModel.isSortAsc()) {
      iconSortDate.setImageResource(R.drawable.ic_arrow_up_333333)
    } else {
      iconSortDate.setImageResource(R.drawable.ic_arrow_down_333333)
    }

    tableScroll.indexRecycler = indexRv
    tableScroll.headerScroll = headerScroll

    headerScroll.setOnTouchListener { _, event ->
      tableScroll.dispatchTouchEvent(event)
      true
    }

    indexRv.setOnTouchListener { _, event ->
      tableScroll.dispatchTouchEvent(event)
      true
    }

    fabAdd.setOnClickListener {
      openAddNoteDialog()
    }

    MoreActionDialog(
      activity = this,
      drawerLayout = drawerLayout,
      onExport = {
        exportCsvLauncher.launch("notes-${System.currentTimeMillis()}.csv")
      },
      onImport = {
        importCsvLauncher.launch(arrayOf("text/csv", "text/*"))
      },
      onExportPdf = {
        exportPdfLauncher.launch("notes-${System.currentTimeMillis()}.pdf")
      }
    ).bind(moreActionMenu)

    val navigationView =
      findViewById<com.google.android.material.navigation.NavigationView>(
        R.id.navigationView
      )

    val headerView = navigationView.getHeaderView(0)
    val btnResetData = headerView.findViewById<Button>(R.id.btnResetData)

    btnResetData.setOnClickListener {
      com.catcode.note_app.ui.dialog.ConfirmResetDialog.show(
        context = this,
        onConfirm = {
          viewModel.deleteAllNotes()
        }
      )
    }

    val etSearch = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.searchInput)

    etSearch.addTextChangedListener(object : android.text.TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        viewModel.searchNotes(s.toString())
      }

      override fun afterTextChanged(s: android.text.Editable?) {}
    })

    layoutSortDate.setOnClickListener {
      viewModel.toggleSortDate()

      if (viewModel.isSortAsc()) {
        iconSortDate.setImageResource(R.drawable.ic_arrow_up_333333)
      } else {
        iconSortDate.setImageResource(R.drawable.ic_arrow_down_333333)
      }
    }
  }

    private fun observeNotes() {
      lifecycleScope.launch {
        viewModel.notes.collect { notes ->
          noteAdapter.submitData(notes)
          indexAdapter.updateCount(notes.size)
        }
      }
    }

    private fun openAddNoteDialog() {
    AddNoteDialog(
      context = this,
      repository = repository,
      lifecycleScope = lifecycleScope,
      onSuccess = {
        viewModel.loadNotes()
      }
    ).show()
  }


  override fun onBackPressed() {
    if (noteAdapter.hasSelection()) {
      noteAdapter.clearSelection()
      indexAdapter.clearSelection()
    } else {
      super.onBackPressed()
    }
  }

  private fun exportCsv(uri: android.net.Uri) {
    viewModel.exportNotes { notes ->
      try {
        contentResolver.openOutputStream(uri)?.let { output ->
          com.catcode.note_app.util.CsvExporter.writeNotesToCsv(
            notes = notes,
            outputStream = output
          )
        }

        AlertDialog.Builder(this@MainActivity)
          .setTitle("Export Successful")
          .setMessage("CSV exported successfully")
          .setPositiveButton("OK") { _, _ -> }
          .show()
      } catch (e: Exception) {
        AlertDialog.Builder(this@MainActivity)
          .setTitle("Export Error")
          .setMessage("Error exporting CSV: ${e.message}")
          .setPositiveButton("OK") { _, _ -> }
          .show()
        e.printStackTrace()
      }
    }
  }

  private fun importCsv(uri: android.net.Uri) {
    lifecycleScope.launch {
      try {
        contentResolver.openInputStream(uri)?.let { input ->
          val notes = com.catcode.note_app.util.CsvImporter.readNotesFromCsv(input)
          
          if (notes.isEmpty()) {
            AlertDialog.Builder(this@MainActivity)
              .setTitle("Import Failed")
              .setMessage("No valid notes found in the CSV file")
              .setPositiveButton("OK") { _, _ -> }
              .show()
            return@launch
          }

          notes.forEach { note ->
            repository.insertNote(note)
          }

          viewModel.loadNotes()

          AlertDialog.Builder(this@MainActivity)
            .setTitle("Import Successful")
            .setMessage("${notes.size} notes imported successfully")
            .setPositiveButton("OK") { _, _ -> }
            .show()
        }
      } catch (e: Exception) {
        AlertDialog.Builder(this@MainActivity)
          .setTitle("Import Error")
          .setMessage("Error importing CSV: ${e.message}")
          .setPositiveButton("OK") { _, _ -> }
          .show()
        e.printStackTrace()
      }
    }
  }

  private fun exportPdf(uri: android.net.Uri) {
    viewModel.exportNotes { notes ->
      try {
        contentResolver.openOutputStream(uri)?.let { output ->
          com.catcode.note_app.util.PdfExporter.writeNotesToPdf(
            notes = notes,
            outputStream = output
          )

          AlertDialog.Builder(this@MainActivity)
            .setTitle("Export Successful")
            .setMessage("PDF exported successfully")
            .setPositiveButton("OK") { _, _ -> }
            .show()
        }
      } catch (e: Exception) {
        AlertDialog.Builder(this@MainActivity)
          .setTitle("Export Error")
          .setMessage("Error exporting PDF: ${e.message}")
          .setPositiveButton("OK") { _, _ -> }
          .show()
        e.printStackTrace()
      }
    }
  }

}
