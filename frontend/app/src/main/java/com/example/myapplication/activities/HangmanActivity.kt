package com.example.myapplication.activities

import Lesson
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.BottomFeedbackHangman
import com.example.myapplication.R
import com.example.myapplication.RetrofitInstance
import com.google.android.flexbox.FlexboxLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HangmanActivity : AppCompatActivity() {
    private lateinit var sharedPreferences:SharedPreferences
    private var retrofitInterface= RetrofitInstance.retrofitInterface
    private lateinit var word:String
    private lateinit var wordCharArray: CharArray
    private lateinit var guessedLetters :CharArray
    private var mistakes=0
    private val maxMistakes=7
    private lateinit var wordContainer:FlexboxLayout
    private lateinit var buttons:List<Button>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hangman)
        sharedPreferences=getSharedPreferences("user_session",Context.MODE_PRIVATE)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        wordContainer=findViewById<FlexboxLayout>(R.id.wordContainer)
        val lessonId=sharedPreferences.getString("current_lesson_id",null)
        val token = sharedPreferences.getString("AUTH_TOKEN", null)
        if (lessonId != null) {
            if (token != null) {
                retrofitInterface.getLessonById(token,lessonId).enqueue(object: Callback<Lesson> {
                    override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                        val lesson= response.body()!!
                        word=lesson.game.words.random()
                        wordCharArray=word.toCharArray()
                        guessedLetters = CharArray(word.length) { '_' }
                        addUnderscores(wordContainer,wordCharArray.size)
                        addClickListenersToButtons()
                    }

                    override fun onFailure(call: Call<Lesson>, t: Throwable) {
                    }

                })
            }


        }
    }
    fun addUnderscores(wordContainer:FlexboxLayout,length: Int) {
        for (i in 0..length-1){
            val textView = TextView(this).apply {
                text = "_"
                textSize = resources.getDimension(R.dimen.letter)
                setPadding(8, 8, 8, 8)
                gravity = Gravity.CENTER
                setTextColor(Color.BLACK)
            }
            wordContainer.addView(textView)
        }

    }
    private fun updateHangmanImage(mistakes: Int) {
        val hangmanImage = findViewById<ImageView>(R.id.hangmanImage)
        val drawableRes = when (mistakes) {
            1 -> R.drawable.hangman1
            2 -> R.drawable.hangman2
            3 -> R.drawable.hangman3
            4 -> R.drawable.hangman4
            5 -> R.drawable.hangman5
            6 -> R.drawable.hangman6
            7->R.drawable.hangman7
            else -> R.drawable.hangman0
        }
        hangmanImage.setImageResource(drawableRes)
    }
    private fun addClickListenersToButtons(){
        buttons=listOf(
        findViewById(R.id.buttonA),
        findViewById(R.id.buttonB),
        findViewById(R.id.buttonV),
        findViewById(R.id.buttonG),
        findViewById(R.id.buttonD),
        findViewById(R.id.buttonDj),
        findViewById(R.id.buttonE),
        findViewById(R.id.buttonZj),
        findViewById(R.id.buttonZ),
        findViewById(R.id.buttonI),
        findViewById(R.id.buttonJ),
        findViewById(R.id.buttonK),
        findViewById(R.id.buttonL),
        findViewById(R.id.buttonLj),
        findViewById(R.id.buttonM),
        findViewById(R.id.buttonN),
        findViewById(R.id.buttonNј),
        findViewById(R.id.buttonO),
        findViewById(R.id.buttonP),
        findViewById(R.id.buttonR),
        findViewById(R.id.buttonS),
        findViewById(R.id.buttonT),
        findViewById(R.id.buttonC2),
        findViewById(R.id.buttonU),
        findViewById(R.id.buttonF),
        findViewById(R.id.buttonH),
        findViewById(R.id.buttonC),
        findViewById(R.id.buttonCh),
        findViewById(R.id.buttonDz),
        findViewById(R.id.buttonSh),
        )
        for (button in buttons){
            button.setOnClickListener {
                checkLetter(button.text.toString()[0],button)
            }
        }


    }
    private fun checkLetter(letter: Char, button: Button) {
        var letterFound = false

        for (i in wordCharArray.indices) {
            if (wordCharArray[i].lowercaseChar() == letter.lowercaseChar()) {
                guessedLetters[i] = letter
                updateWordContainer(i, wordCharArray[i])
                letterFound = true
            }
        }

        if (letterFound) {
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#58D68D"))
        } else {
            mistakes++
            button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E74C3C"))
            updateHangmanImage(mistakes)
        }
        button.isClickable = false

        checkGameStatus()
    }
    private fun checkGameStatus() {
        if (!guessedLetters.contains('_')) {
            for(button in buttons){
                button.isClickable=false
            }
            val feedbackFragment = BottomFeedbackHangman.newInstance(true,"")
            feedbackFragment.show(supportFragmentManager, "FeedbackHangman")

        } else if (mistakes >= maxMistakes) {
            for(button in buttons){
                button.isClickable=false
            }
            for(i in 0..word.length-1){
                updateWordContainer(i, word[i])
            }
            val feedbackFragment = BottomFeedbackHangman.newInstance(false,word)
            feedbackFragment.show(supportFragmentManager, "FeedbackHangman")

        }

    }
    fun updateWordContainer(position: Int, letter: Char) {
        val textView = wordContainer.getChildAt(position) as TextView
        textView.text = letter.toString()
    }
}
