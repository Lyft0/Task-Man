package com.example.taskman.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.taskman.R

class CreateBoardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        setupActionBar()
    }

    private fun setupActionBar() {
        val toolbarCreateBoard = findViewById<Toolbar>(R.id.toolbar_create_board_activity)
        setSupportActionBar(toolbarCreateBoard)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_arrow_back)
            actionBar.title = "Create Board"
        }

        toolbarCreateBoard.setNavigationOnClickListener { onBackPressed() }
    }
}