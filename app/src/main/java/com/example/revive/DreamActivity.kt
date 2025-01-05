package com.example.revive

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.example.revive.databinding.ActivityDreamBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DreamActivity : FilePicker() {

    var currentDate: Calendar? = null

    // layout/activity_main.xml -> ActivityMainBinding usw.
    private lateinit var binding: ActivityDreamBinding

    @SuppressLint("ClickableViewAccessibility") // cf https://stackoverflow.com/questions/47107105/android-button-has-setontouchlistener-called-on-it-but-does-not-override-perform
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Zugriff auf Steuerelemente via binding
        binding = ActivityDreamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get date of last year
        val wantedDate = Calendar.getInstance()
        wantedDate.add(Calendar.YEAR, -1)

        // Set text information
        val mainText = findViewById<View>(R.id.main_text) as TextView
        val diaryText = "Heute vor einem Jahr hast du geträumt, dass ${findText(wantedDate)}"
        mainText.text = diaryText

        // Set gestures
        mainText.setOnTouchListener(object : OnSwipeTouchListener(this@DreamActivity) {
            override fun onSwipeRight() {
                val wantedDateGesture = currentDate
                wantedDateGesture?.add(Calendar.DAY_OF_MONTH, -1)

                val yearWanted = wantedDateGesture?.get(Calendar.YEAR)
                val monthWanted = wantedDateGesture?.get(Calendar.MONTH)
                val dayWanted = wantedDateGesture?.get(Calendar.DAY_OF_MONTH)

                setTextByCalendarDate(yearWanted, monthWanted, dayWanted)
            }

            override fun onSwipeLeft() {
                val wanteddateGesture = currentDate
                wanteddateGesture?.add(Calendar.DAY_OF_MONTH, 1)

                val yearWanted = wanteddateGesture?.get(Calendar.YEAR)
                val monthWanted = wanteddateGesture?.get(Calendar.MONTH)
                val dayWanted = wanteddateGesture?.get(Calendar.DAY_OF_MONTH)

                setTextByCalendarDate(yearWanted, monthWanted, dayWanted)
            }

            override fun onSwipeBottom() {
                val intent = Intent(this@DreamActivity, MainActivity::class.java)
                startActivity(intent)
            }
        })

        // Set image
        val mainImage = findViewById<View>(R.id.main_image) as ImageView
        mainImage.setImageResource(R.drawable.placeholder)

        // Set Float Action Butttons
        binding.jumpToDateButton.setOnClickListener { openYearSelection() }
        binding.setDateButton.setOnClickListener { openCalendar() }
        binding.resetButton.setOnClickListener { resetApp() }

    }

    private fun resetApp() {
        val intent = Intent(this, DreamActivity::class.java)
        this.startActivity(intent)
        ActivityCompat.finishAffinity(this)
    }

    private fun openCalendar() {
        // Get current date
        val now = Calendar.getInstance()
        val yearNow = now.get(Calendar.YEAR)
        val monthNow = now.get(Calendar.MONTH)
        val dayNow = now.get(Calendar.DAY_OF_MONTH)

        // Open Datepicker
        val datePickerDialog = DatePickerDialog(
            this@DreamActivity, { _, year, month, dayOfMonth ->
                setTextByCalendarDate(year, month, dayOfMonth)
            }, yearNow, monthNow, dayNow
        )
        datePickerDialog.show()
    }

    fun setTextByCalendarDate(year: Int?, month: Int?, dayOfMonth: Int?) {
        val wantedDate = Calendar.getInstance()
        wantedDate.set(Calendar.YEAR, year!!)
        wantedDate.set(Calendar.MONTH, month!!)
        wantedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth!!)

        val wantedDateFormatted =
            SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(wantedDate.time)
        val mainText = findViewById<View>(R.id.main_text) as TextView
        val diaryText = "Am $wantedDateFormatted hast du geträumt, dass ${findText(wantedDate)}"
        mainText.text = diaryText
    }

    private fun openYearSelection() {
        // Get current date
        val now = Calendar.getInstance()
        val yearNow = now.get(Calendar.YEAR)
        val monthNow = now.get(Calendar.MONTH)
        val dayNow = now.get(Calendar.DAY_OF_MONTH)

        // Open Numberpicker
        val numberPicker = NumberPicker(this)

        numberPicker.minValue = 2014
        numberPicker.maxValue = yearNow
        numberPicker.wrapSelectorWheel = true
        numberPicker.value = yearNow - 2

        val valueChangeListener: NumberPicker.OnValueChangeListener? = null

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Wähle ein Jahr")

        builder.setPositiveButton("OK") { _, _ ->
            setTextByCalendarDate(numberPicker.value, monthNow, dayNow)
        }
        builder.setNegativeButton("Abbrechen") { _, _ ->
            valueChangeListener?.onValueChange(
                numberPicker,
                numberPicker.value, numberPicker.value
            )
        }

        builder.setView(numberPicker)
        builder.show()
    }

    private fun getTomorrow12Clock(): Calendar {
        return Calendar.getInstance().also {
            it.add(Calendar.DAY_OF_MONTH, 1)
            it.set(Calendar.HOUR_OF_DAY, 12)
            it.set(Calendar.MINUTE, 0)
            it.set(Calendar.SECOND, 0)
        }
    }

    private fun findText(wantedDate: Calendar): String {
        // get text file from Google Drive
        val fileText: List<String> = readDiary("traumtagebuch", this)
        val wantedDateFormatted =
            SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(wantedDate.time)
        val diaryText = fileText.find { it.contains(wantedDateFormatted) }
        this.currentDate = wantedDate
        return when (diaryText == null) {
            true -> "..."
            false -> diaryText.substring(11, diaryText.length)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_search -> openSearchBar()
            R.id.action_update_diary -> {
                openFilePickerAndSaveToInternalStorage("tagebuch.txt")
                return true
            }
            R.id.action_update_dreamdiary -> {
                openFilePickerAndSaveToInternalStorage("traumtagebuch.txt")
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openSearchBar(): Boolean {
        onSearchRequested()
        return true
    }
}
