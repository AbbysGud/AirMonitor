package com.example.airmonitorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class Product : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        val btn_back = findViewById<ImageView>(R.id.product_btn_back)

        btn_back.setOnClickListener {
            val pindah = Intent(this, Fragment::class.java)
            pindah.putExtra("TARGET_FRAGMENT", "fProfile")
            startActivity(pindah)
        }
    }
}