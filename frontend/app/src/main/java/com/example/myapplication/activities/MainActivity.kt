package com.example.myapplication.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.example.myapplication.LessonsFragment
import com.example.myapplication.R
import com.example.myapplication.SelectGradeFragment
import com.example.myapplication.SettingsFragment
import com.example.myapplication.models.Child
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var child:Child?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        Log.d("Moje poruke", "u main-u smo")
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)






        val selectGradeFragment= SelectGradeFragment()
        val lessonsFragment= LessonsFragment()
        val settingsFragment= SettingsFragment()
        child=getChildFromSharedPreference()
        if(child==null){
            Intent(this,SelectChildActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
        if(child?.selectedGrade!=0){
            setCurrentFragment(lessonsFragment)
        }
        else{
            Log.d("Moje poruke deteeee",child.toString())
            setCurrentFragment(selectGradeFragment)
            disableLessons()
        }
        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.itemTextColor = null
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navClass->{
                    setCurrentFragment(selectGradeFragment)
                    true
                }
                R.id.navLessons->{
                    setCurrentFragment(lessonsFragment)
                    true
                }
                R.id.navSettings->{
                    setCurrentFragment(settingsFragment)
                    true
                }
                else->false
            }
        }


    }

    private fun disableLessons() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navLessonsItem = bottomNav.menu.findItem(R.id.navLessons)

        navLessonsItem.isEnabled = false

        navLessonsItem.icon?.alpha = 128 // Vrednost između 0 (prozirno) i 255 (neprozirno)
    }

    fun enableLessons() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navLessonsItem = bottomNav.menu.findItem(R.id.navLessons)

        navLessonsItem.isEnabled = true

        navLessonsItem.icon?.alpha = 0 // Vrednost između 0 (prozirno) i 255 (neprozirno)
    }

    fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.flMain, fragment)
            .commit()
    }
    fun getChildFromSharedPreference(): Child? {
        val gson = Gson()
        val childJson = sharedPreferences.getString("child", null)

        return if (childJson != null) {
            gson.fromJson(childJson, Child::class.java)
        } else {
            null
        }
    }



}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}
