package com.example.revive

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

fun readDiary(diary: String, ctx: Context): List<String> {
    val file = File(ctx.filesDir, "$diary.txt")

    // Check if the file exists
    if (!file.exists()) {
        return emptyList() // Return an empty list if the file is not found
    }

    // If the file exists, read its content
    return file.bufferedReader().use { reader ->
        reader.readLines()
    }
}
