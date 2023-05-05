package com.apptest.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apptest.R
import kotlinx.android.synthetic.main.activity_response.*

class ResponseActivity : AppCompatActivity() {
    private var response = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response)
        supportActionBar?.title = getString(R.string.title_response_activity)

        val extras = intent.extras
        if (extras != null) {
            response = extras.getString("response").toString()
        }
        txtResponse.text = response
    }
}