package hr.kotlin.rss_reader_android_app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ArticleActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val articleText = intent.getStringExtra("article")
        val articleTextView = findViewById<TextView>(R.id.articleTextView)
        articleTextView.text = articleText

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
        }
    }

