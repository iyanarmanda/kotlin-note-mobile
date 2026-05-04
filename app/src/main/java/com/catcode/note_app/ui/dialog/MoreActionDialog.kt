package com.catcode.note_app.ui.dialog

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.drawerlayout.widget.DrawerLayout
import com.catcode.note_app.R

class MoreActionDialog(
  private val activity: Activity,
  private val drawerLayout: DrawerLayout,
  private val onExport: () -> Unit,
  private val onImport: () -> Unit = {},
  private val onExportPdf: () -> Unit = {}
) {

  fun bind(menuView: ImageView) {
    menuView.setOnClickListener { view ->
      showMenu(view)
    }
  }

  private fun showMenu(anchor: View) {
    val popup = PopupMenu(activity, anchor)
    popup.inflate(R.menu.more_action_menu)

    popup.setOnMenuItemClickListener { item ->
      when (item.itemId) {
        R.id.action_import -> {
          onImport()
          true
        }
        R.id.action_export -> {
          onExport()
          true
        }
        R.id.action_export_pdf -> {
          onExportPdf()
          true
        }
        R.id.action_about -> {
          drawerLayout.openDrawer(Gravity.START)
          true
        }
        R.id.action_exit -> {
          activity.finish()
          true
        }
        else -> false
      }
    }

    popup.show()
  }

  private fun onImport() {
    onImport.invoke()
  }
}
