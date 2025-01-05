package com.example.revive

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

open class FilePicker : AppCompatActivity() {
    protected fun openFilePickerAndSaveToInternalStorage(fileName: String) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain" // Filter for TXT files
        }

        if (fileName == "tagebuch.txt") {
            startActivityForResult(intent, REQUEST_CODE_PICK_DIARY_TXT_FILE)
        } else if (fileName == "traumtagebuch.txt") {
            startActivityForResult(intent, REQUEST_CODE_PICK_DREAMDIARY_TXT_FILE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_DIARY_TXT_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                saveFileToInternalStorage(uri, "tagebuch.txt")
            }
        } else if (requestCode == REQUEST_CODE_PICK_DREAMDIARY_TXT_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                saveFileToInternalStorage(uri, "traumtagebuch.txt")
            }
        }
    }

    private fun saveFileToInternalStorage(uri: Uri, fileName: String) {
        val file = File(filesDir, fileName)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        // Optionally, notify the user or update the UI
        Toast.makeText(this, "(Traum-)Tagebuch aktualisiert", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CODE_PICK_DIARY_TXT_FILE = 176 // Choose a unique request code
        private const val REQUEST_CODE_PICK_DREAMDIARY_TXT_FILE = 177 // Choose a unique request code
    }
}