package ru.andreikud.mvvmtodo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andreikud.mvvmtodo.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}