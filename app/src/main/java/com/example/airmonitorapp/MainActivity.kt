package com.example.airmonitorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bMove = findViewById<Button>(R.id.main_btn_move)

        bMove.setOnClickListener {
            val pindah = Intent(this, Fragment::class.java)
            pindah.putExtra("TARGET_FRAGMENT", "fHome")
            startActivity(pindah)
        }
    }
}