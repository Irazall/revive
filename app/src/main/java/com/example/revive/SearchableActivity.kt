package com.example.revive

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.revive.databinding.ActivitySearchableBinding

class SearchableActivity : AppCompatActivity() {
    // layout/activity_main.xml -> ActivityMainBinding usw.
    private lateinit var binding: ActivitySearchableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Zugriff auf Steuerelemente via binding
        binding = ActivitySearchableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set Float Action Butttons
        binding.goBackButton.setOnClickListener { goBack() }

    }

    private fun goBack() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        ActivityCompat.finishAffinity(this)
    }

}
