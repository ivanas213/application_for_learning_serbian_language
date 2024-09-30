package com.example.myapplication.activities

import Lesson
import QuestionBlock
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.example.myapplication.BottomDialogFragmentTest
import com.example.myapplication.FeedbackBottomSheetFragment
import com.example.myapplication.R
import com.example.myapplication.RetrofitInstance
import com.example.myapplication.models.Child
import com.example.myapplication.models.MessageResult
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.xml.KonfettiView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class QuestionsActivity : AppCompatActivity(), FeedbackBottomSheetFragment.OnDismissListener {
    private var questions: List<QuestionBlock> = listOf()
    private var currentQuestionIndex = 0
    private lateinit var sharedPreferences:SharedPreferences
    private var retrofitInterface= RetrofitInstance.retrofitInterface
    private var selectedLeftButton:Button?=null
    private var selectedRightButton:Button?=null
    private var matched=0
    private var isTest=false
    private var totalScore=0
    private var possibleScore=0
    private lateinit var konfettiView:KonfettiView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_questions)
        sharedPreferences=getSharedPreferences("user_session",Context.MODE_PRIVATE)
        isTest=intent.getBooleanExtra("isTest",true)
        konfettiView= findViewById(R.id.konfettiView)
        loadQuestions()
    }
    fun getCurrentLesson(): Lesson? {
        val json = sharedPreferences.getString("current_lesson", null)
        return if (json != null) {
            Gson().fromJson(json, Lesson::class.java)
        } else {
            null
        }
    }
    private fun getChild(): Child? {
        val json = sharedPreferences.getString("child", null)
        return if (json != null) {
            Gson().fromJson(json, Child::class.java)
        } else {
            null
        }
    }
    private fun loadQuestions(){
        if(!isTest){
            val id=getCurrentLesson()?._id
            val json = sharedPreferences.getString("questions$id", null)
            if (json != null) {
                val gson = Gson()
                val type = object : TypeToken<List<QuestionBlock>>() {}.type
                questions=gson.fromJson(json, type)
                questions= questions.shuffled()
            }
            else {
                questions=listOf()
            }
            showNextQuestion()
        }
        else{
            val topic = intent.getStringExtra("topic")
            val token=sharedPreferences.getString("AUTH_TOKEN",null)
            if (token!=null && topic!=null) {
                retrofitInterface.getTestQuestions("Bearer $token",topic).enqueue(object:Callback<List<QuestionBlock>>{

                    override fun onResponse(
                        call: Call<List<QuestionBlock>>,
                        response: Response<List<QuestionBlock>>
                    ) {
                        questions= response.body()!!
                        showNextQuestion()
                    }

                    override fun onFailure(call: Call<List<QuestionBlock>>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                }
                )
            }

        }

    }
    private fun showNextQuestion() {

        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            question._try=1
            val questionIndicator: TextView = findViewById(R.id.questionIndicator)
            questionIndicator.text = "Питање ${currentQuestionIndex + 1} од ${questions.size}"
            val questionContainer: LinearLayout = findViewById(R.id.questionContainer)
            questionContainer.removeAllViews()
            when (question.questionType) {
                "classic" -> showClassicQuestion(question)
                "fill" -> showFillQuestion(question)
                "true_false" -> showTrueFalseQuestion(question)
                "multiple_choice" -> showMultipleChoiceQuestion(question)
                "spelling_correction"->showSpellingCorrectionQuestion(question)
                "matching"->showMatchingQuestion(question)
            }
        }
        else if(!isTest) {

            val token = sharedPreferences.getString("AUTH_TOKEN", null)
            val lesson=sharedPreferences.getString("current_lesson_id",null)
            val map=HashMap<String,String>()
            if (lesson != null) {
                map["lessonId"]=lesson
            }
            var child=getChild()
            if(token!=null){
                val call: Call<MessageResult>? =
                    child?.let { retrofitInterface.completedLesson("Bearer $token", it._id,map) }
                try {
                    call?.enqueue(object : Callback<MessageResult>{
                        override fun onResponse(call: Call<MessageResult>, response: Response<MessageResult>) {
                            val callForGetChild:Call<Child>?= child?.let {
                                retrofitInterface.getChildById("Bearer $token",
                                    it._id)
                            }
                            callForGetChild?.enqueue(object : Callback<Child>{
                                override fun onResponse(call: Call<Child>, response: Response<Child>) {
                                    child=response.body()
                                    val gson = Gson()
                                    val childJson = gson.toJson(child)
                                    with(sharedPreferences.edit()) {
                                        putString("child", childJson)
                                        apply()
                                    }
                                    Intent(this@QuestionsActivity,MainActivity::class.java).also {
                                        startActivity(it)
                                        finish()
                                    }
                                }

                                override fun onFailure(call: Call<Child>, t: Throwable) {
                                    Intent(this@QuestionsActivity,MainActivity::class.java).also {
                                        startActivity(it)
                                        finish()
                                    }
                                }

                            })
                        }

                        override fun onFailure(call: Call<MessageResult>, t: Throwable) {
                            Toast.makeText(this@QuestionsActivity,"Грешка на серверу $t",Toast.LENGTH_SHORT).show()
                            Intent(this@QuestionsActivity,MainActivity::class.java).also {
                                startActivity(it)
                                finish()
                            }
                        }
                    })
                } catch (e: Exception) {
                    Toast.makeText(this@QuestionsActivity,"Грешка $e",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                logout()
            }

        }
        else{
            val points:Double=(100.0*totalScore)/possibleScore
            val token=sharedPreferences.getString("AUTH_TOKEN",null)
            var child= getChild()
            var childId= child?._id
            val map= HashMap<String, String>()
            map["topicId"]=questions[0].topic
            map["result"]=points.toInt().toString()
            if (child != null) {
                if (childId != null) {
                    retrofitInterface.completedTest("Bearer $token", childId,map).enqueue(
                        object:Callback<MessageResult>{
                            override fun onResponse(
                                call: Call<MessageResult>,
                                response: Response<MessageResult>
                            ) {
                                val callForGetChild:Call<Child>?= child?.let {
                                    retrofitInterface.getChildById("Bearer $token",
                                        childId)
                                }
                                callForGetChild?.enqueue(object : Callback<Child>{
                                    override fun onResponse(call: Call<Child>, response: Response<Child>) {
                                        val childFull=response.body()
                                        if (childFull != null) {
                                            child=childFull
                                        }
                                        val gson = Gson()
                                        val childJson = gson.toJson(child)
                                        with(sharedPreferences.edit()) {
                                            putString("child", childJson)
                                            apply()
                                        }

                                        if(points>=90) {
                                            val parties = listOf(
                                                Party(
                                                    speed = 10f,
                                                    maxSpeed = 60f,
                                                    damping = 0.6f,
                                                    spread = 1,
                                                    colors = listOf(
                                                        0xfff200,
                                                        0xff004d,
                                                        0x00baff,
                                                        0x8aff00,
                                                        0xffa6ff,
                                                        0xff5722
                                                    ),
                                                    position = Position.Relative(
                                                        0.5,
                                                        0.0
                                                    ),
                                                    emitter = Emitter(
                                                        duration = 1000,
                                                        TimeUnit.MILLISECONDS
                                                    ).perSecond(200),
                                                    rotation = Rotation.enabled()
                                                ),
                                                Party(
                                                    speed = 10f,
                                                    maxSpeed = 60f,
                                                    damping = 0.6f,
                                                    spread = 180,
                                                    colors = listOf(
                                                        0xfff200,
                                                        0xff004d,
                                                        0x00baff,
                                                        0x8aff00,
                                                        0xffa6ff,
                                                        0xff5722
                                                    ),
                                                    position = Position.Relative(
                                                        0.5,
                                                        1.0
                                                    ),
                                                    emitter = Emitter(
                                                        duration = 1000,
                                                        TimeUnit.MILLISECONDS
                                                    ).perSecond(200),
                                                    rotation = Rotation.enabled()
                                                ),
                                                Party(
                                                    speed = 10f,
                                                    maxSpeed = 60f,
                                                    damping = 0.6f,
                                                    spread = 270,
                                                    colors = listOf(
                                                        0xfff200,
                                                        0xff004d,
                                                        0x00baff,
                                                        0x8aff00,
                                                        0xffa6ff,
                                                        0xff5722
                                                    ),
                                                    position = Position.Relative(
                                                        0.5,
                                                        0.5
                                                    ),
                                                    emitter = Emitter(
                                                        duration = 1000,
                                                        TimeUnit.MILLISECONDS
                                                    ).perSecond(200),
                                                    rotation = Rotation.enabled()
                                                ),
                                                Party(
                                                    speed = 10f,
                                                    maxSpeed = 60f,
                                                    damping = 0.6f,
                                                    spread = 360,
                                                    colors = listOf(
                                                        0xfff200,
                                                        0xff004d,
                                                        0x00baff,
                                                        0x8aff00,
                                                        0xffa6ff,
                                                        0xff5722
                                                    ),
                                                    position = Position.Relative(
                                                        0.0,
                                                        0.5
                                                    ),
                                                    emitter = Emitter(
                                                        duration = 1000,
                                                        TimeUnit.MILLISECONDS
                                                    ).perSecond(200),
                                                    rotation = Rotation.enabled()
                                                ),
                                                Party(
                                                    speed = 10f,
                                                    maxSpeed = 60f,
                                                    damping = 0.6f,
                                                    spread = 45,
                                                    colors = listOf(
                                                        0xfff200,
                                                        0xff004d,
                                                        0x00baff,
                                                        0x8aff00,
                                                        0xffa6ff,
                                                        0xff5722
                                                    ),
                                                    position = Position.Relative(
                                                        1.0,
                                                        0.5
                                                    ),
                                                    emitter = Emitter(
                                                        duration = 1000,
                                                        TimeUnit.MILLISECONDS
                                                    ).perSecond(200),
                                                    rotation = Rotation.enabled()
                                                )
                                            )

                                            parties.forEach { party ->
                                                konfettiView.start(party)
                                            }
                                            val handler = Handler(Looper.getMainLooper())
                                            handler.postDelayed({
                                                val feedbackFragment = BottomDialogFragmentTest.newInstance(points.toInt())
                                                feedbackFragment.show(supportFragmentManager, "BottomFragmentTest")
                                            }, 3000)
                                        } else{
                                            val feedbackFragment = BottomDialogFragmentTest.newInstance(points.toInt())
                                            feedbackFragment.show(supportFragmentManager, "BottomFragmentTest")
                                        }

                                    }

                                    override fun onFailure(call: Call<Child>, t: Throwable) {
                                    }

                                })

                            }

                            override fun onFailure(call: Call<MessageResult>, t: Throwable) {
                                Intent(this@QuestionsActivity,MainActivity::class.java).also {
                                    startActivity(it)
                                    finish()
                                }
                            }

                        }
                    )
                }
            }

        }
    }
    private fun showClassicQuestion(question: QuestionBlock) {
        val questionContainer: LinearLayout = findViewById(R.id.questionContainer)
        val classicLayout = layoutInflater.inflate(R.layout.item_question_classic, questionContainer, false)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        classicLayout.layoutParams = layoutParams
        val twQuestion = classicLayout.findViewById<TextView>(R.id.twClassicQuestion)
        twQuestion.text = question.question
        val btnSubmit=classicLayout.findViewById<Button>(R.id.btnClassicSubmit)
        val answer=classicLayout.findViewById<EditText>(R.id.etClassicAnswer)
        btnSubmit.setOnClickListener {
            handleClassicAnswer(question,answer.text.toString(), question.correctAnswers)
        }
        questionContainer.addView(classicLayout)
    }
    private fun showFillQuestion(question: QuestionBlock) {
        val questionContainer: LinearLayout = findViewById(R.id.questionContainer)
        val fillLayout = layoutInflater.inflate(R.layout.item_question_fill, questionContainer, false)

        val fillQuestionContainer = fillLayout.findViewById<FlexboxLayout>(R.id.fillQuestionContainer)
        val questionHeader = question.question
        val questionText = question.text
        val correctAnswers = question.correctAnswers
        val questionTextView = fillLayout.findViewById<TextView>(R.id.headerTextView)

        questionTextView.text = questionHeader

        val parts = questionText.split(" ")
        var ind=0
        for (i in parts.indices) {
            if(parts[i]!="_"){
                val textView = TextView(this).apply {
                    text = parts[i]+" "
                    textSize = resources.getDimension(R.dimen.fill_text_size)
                    isSingleLine = true
                    setTextColor(Color.BLACK)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 20)
                    }
                }

                fillQuestionContainer.addView(textView)

            }
            else{
                val correctAnswer = correctAnswers[ind]
                ind+=1
                val editText = EditText(this).apply {
                    textSize = resources.getDimension(R.dimen.fill_text_size)
                    gravity = Gravity.CENTER
                    setPadding(16, 8, 16, 8)

                    val editTextWidth = paint.measureText(correctAnswer).toInt() + 100
                    layoutParams = LinearLayout.LayoutParams(
                        editTextWidth,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(12, 0, 12, 20)
                    }

                    val backgroundDrawable = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 12f
                        setColor(Color.WHITE)
                        setStroke(3, Color.BLACK)
                    }
                    background = backgroundDrawable
                }

                fillQuestionContainer.addView(editText)
            }


        }

        val btnSubmit = fillLayout.findViewById<Button>(R.id.btnFillSubmit).apply {
            textSize = 24f
            setPadding(20, 20, 20, 20)
        }
        var isFilled = true
        btnSubmit.setOnClickListener {
            isFilled=true
            var allCorrect = true
            var answerIndex = 0

            for (i in 0 until fillQuestionContainer.childCount) {
                val subView = fillQuestionContainer.getChildAt(i)
                if (subView is EditText) {
                    subView.error = null
                }
            }

            for (i in 0 until fillQuestionContainer.childCount) {
                val drawable = GradientDrawable()
                drawable.setStroke(2, Color.RED)
                drawable.cornerRadius = 4f

                val subView = fillQuestionContainer.getChildAt(i)
                if (subView is EditText) {
                    val userAnswer = subView.text.toString().trim()
                    if (userAnswer.isEmpty()) {
                        isFilled = false
                        allCorrect = false
                        subView.background = drawable
                        subView.error = "Празно поље"
                        continue
                    }
                    if (answerIndex < correctAnswers.size) {
                        val correctAnswer = correctAnswers[answerIndex]
                        if (!userAnswer.equals(correctAnswer, ignoreCase = true)) {
                            allCorrect = false
                            subView.background = drawable
                            subView.error = "Netacno"
                        }
                        answerIndex++
                    }
                }
            }

            if (isFilled) {
                handleAnswer(question, allCorrect)
            } else {
                val existingErrorView = fillQuestionContainer.findViewWithTag<TextView>("errorTextView")
                if (existingErrorView == null) {
                    val textView = TextView(this).apply {
                        text = "\nПопуни сва поља!"
                        textSize = 28f
                        setTextColor(Color.RED)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 0, 0, 20)
                        }
                        tag = "errorTextView"
                    }
                    fillQuestionContainer.addView(textView)
                }
            }
        }

        questionContainer.addView(fillLayout)
    }
    private fun showTrueFalseQuestion(question: QuestionBlock){
        val questionContainer: LinearLayout = findViewById(R.id.questionContainer)
        val tfLayout = layoutInflater.inflate(R.layout.item_question_true_false, questionContainer, false)
        val twQuestion = tfLayout.findViewById<TextView>(R.id.twTrueFalseQuestion)
        val yesButton=tfLayout.findViewById<Button>(R.id.btnTFTrue)
        val noButton=tfLayout.findViewById<Button>(R.id.btnTFFalse)
        twQuestion.text = question.question

        yesButton.setOnClickListener {
            handleTrueFalseAnswer(question,true)
        }
        noButton.setOnClickListener {
            handleTrueFalseAnswer(question,false)
        }
        yesButton.setOnLongClickListener {
            yesButton.setBackgroundColor(Color.parseColor("#FFAED581"))
            true
        }
        noButton.setOnLongClickListener {
            noButton.setBackgroundColor(Color.parseColor("#FFEF9A9A"))
            true
        }
        questionContainer.addView(tfLayout)
    }
    private fun showMultipleChoiceQuestion(question: QuestionBlock) {
        val questionContainer: LinearLayout = findViewById(R.id.questionContainer)
        val multipleChoiceLayout = layoutInflater.inflate(R.layout.item_question_multiple_choice, questionContainer, false)

        val questionText = multipleChoiceLayout.findViewById<TextView>(R.id.questionText)
        questionText.text = question.question

        val optionsContainer = multipleChoiceLayout.findViewById<LinearLayout>(R.id.optionsContainer)

        val correctAnswer = question.correctAnswerMC
        var i=0
        val shuffled_options=question.options.shuffled()
        shuffled_options.forEach { optionText ->
            val optionButton = Button(this).apply {
                isAllCaps = false
                text = optionText
                textSize = resources.getDimension(R.dimen.multiple_choice_options)
                setTextColor(Color.WHITE)
                setPadding(16, 16, 16, 16)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
            }

            if(i%4==0){
                optionButton.setBackgroundColor(getColor(R.color.option_a))
            }
            else if (i%4==1){
                optionButton.setBackgroundColor(getColor(R.color.option_b))
            }
            else if (i%4==2){
                optionButton.setBackgroundColor(getColor(R.color.option_c))
            }
            else if (i%4==3){
                optionButton.setBackgroundColor(getColor(R.color.option_d))
            }
            i+=1
            optionButton.setOnClickListener {
                handleMultipleChoiceAnswer(question, optionText, correctAnswer)
            }

            optionsContainer.addView(optionButton)
        }

        questionContainer.addView(multipleChoiceLayout)
    }
    private fun showSpellingCorrectionQuestion(question: QuestionBlock) {
        val questionContainer: LinearLayout = findViewById(R.id.questionContainer)
        val spellingLayout = layoutInflater.inflate(R.layout.item_question_spelling_correction, questionContainer, false)
        val textViewQuestion: TextView = spellingLayout.findViewById(R.id.twSCQuestion)
        val editTextAnswer: EditText = spellingLayout.findViewById(R.id.etSCAnswer)
        val buttonSubmit: Button = spellingLayout.findViewById(R.id.btnSCSubmit)
        textViewQuestion.text = question.question
        editTextAnswer.setText(question.incorrectSpelling)
        buttonSubmit.setOnClickListener {
            val userAnswer = editTextAnswer.text.toString().trim()
            val isCorrect = userAnswer.equals(question.correctedSpelling)
            handleAnswer(question, isCorrect)
        }
        questionContainer.addView(spellingLayout)
    }
    private fun showMatchingQuestion(question: QuestionBlock) {
        val questionContainer: LinearLayout = findViewById(R.id.questionContainer)
        val matchingLayout = layoutInflater.inflate(R.layout.item_question_matching, questionContainer, false)
        matchingLayout.findViewById<TextView>(R.id.questionText).text=question.question
        var correctPairs: MutableMap<String, String> = mutableMapOf()
        for (i in question.left.indices) {
            correctPairs[question.left[i]] = question.right[i]
        }

        val leftItems = question.left.shuffled()
        val rightItems = question.right.shuffled()
        val leftItemsList=matchingLayout.findViewById<ListView>(R.id.leftItemsList)
        val rightItemsList=matchingLayout.findViewById<ListView>(R.id.rightItemsList)
        setUpListWithButtons(leftItems, leftItemsList, isLeftSide = true, correctPairs,this,question)
        setUpListWithButtons(rightItems, rightItemsList, isLeftSide = false, correctPairs,this,question)
        questionContainer.addView(matchingLayout)
    }
    private fun setUpListWithButtons(items: List<String>, listView: ListView, isLeftSide: Boolean, correctPairs: MutableMap<String, String>,context:Context,question: QuestionBlock) {
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val button = Button(context).apply {
                    text = getItem(position)
                    textSize = resources.getDimension(R.dimen.matching)
                    setPadding(16, 16, 16, 16)
                    isClickable = true
                    setBackgroundColor(Color.parseColor("#24549e"))
                    setTextColor(Color.WHITE)
                    setAllCaps(false)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 16, 8, 16)
                    }
                    background = ContextCompat.getDrawable(context, R.drawable.rounded_button)
                }
                button.setOnClickListener {
                    if (isLeftSide) {
                        selectedLeftButton?.setBackgroundColor(Color.parseColor("#24549e"))
                        selectedLeftButton = button
                        button.setBackgroundColor(Color.parseColor("#0000ff"))
                    } else {
                        selectedRightButton = button
                        selectedLeftButton?.setBackgroundColor(Color.parseColor("#24549e"))
                        checkPairing(correctPairs, question)
                    }
                }

                return button
            }
        }

        listView.adapter = adapter
    }
    private fun playWrongSound(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.sound_incorrect)
        mediaPlayer.start()
    }
    private fun playCorrectSound(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.sound_correct)
        mediaPlayer.start()
    }
    private fun checkPairing(correctPairs: MutableMap<String, String>, question: QuestionBlock) {
        if (selectedLeftButton != null && selectedRightButton != null) {
            val leftText = selectedLeftButton!!.text.toString()
            val rightText = selectedRightButton!!.text.toString()

            if (correctPairs[leftText] == rightText) {
                matched+=1
                if(matched==correctPairs.size){
                    matched=0
                    handleAnswer(question,true)
                }
                playCorrectSound(this)
                selectedLeftButton!!.setBackgroundColor(Color.parseColor("#2d9ff5"))
                selectedRightButton!!.setBackgroundColor(Color.parseColor("#2d9ff5"))
                selectedLeftButton!!.isClickable = false
                selectedRightButton!!.isClickable = false
            } else {
                playWrongSound(this)
                shakeButton(selectedLeftButton!!)
                shakeButton(selectedRightButton!!)
            }


            selectedLeftButton = null
            selectedRightButton = null
        }
    }
    private fun shakeButton(button: Button) {
        val shake = ObjectAnimator.ofFloat(button, "translationX", 0f, 25f, -25f, 0f)
        shake.duration = 500
        shake.start()
    }
    private fun handleClassicAnswer(question: QuestionBlock,answer: String, correctAnswers: List<String>) {
        if (correctAnswers.any { it.equals(answer.trim(), ignoreCase = true) }) {
            handleAnswer(question,true)
        } else {
            handleAnswer(question,false)
        }
    }

    private fun handleTrueFalseAnswer(question: QuestionBlock,answer: Boolean,){
        handleAnswer(question,!(answer xor question.correctAnswer))
    }
    private fun handleMultipleChoiceAnswer(question: QuestionBlock,selectedAnswer: String, correctAnswer: String) {
        if (selectedAnswer == correctAnswer) {
            handleAnswer(question,true)
        } else {
            handleAnswer(question,false)
        }
    }
    private fun handleAnswer(question: QuestionBlock,isCorrect: Boolean) {
        if(!isTest){
            if (isCorrect) {
                val map=HashMap<String,Int>()
                map["attempts"]=question._try
                val token = sharedPreferences.getString("AUTH_TOKEN", null)
                val call: Call<MessageResult> =retrofitInterface.correctAnswer("Bearer $token",question._id,map)
                call.enqueue(object : Callback<MessageResult> {
                    override fun onResponse(call: Call<MessageResult>, response: Response<MessageResult>) {
                    }

                    override fun onFailure(call: Call<MessageResult>, t: Throwable) {
                        Toast.makeText(this@QuestionsActivity,"Догодила се грешка на серверу $t.",Toast.LENGTH_SHORT).show()
                    }

                })
            } else {
                questions+=question
                question._try+=1
            }

            var correctAnswer=""
            when(question.questionType){
                "true_false"->{
                }
                "multiple_choice"->{
                    correctAnswer = question.correctAnswerMC

                }
                "fill"->{
                    var modifiedText = question.text
                    for (answer in question.correctAnswers) {
                        modifiedText = modifiedText.replaceFirst("_", answer)
                    }
                    correctAnswer = modifiedText

                }
                "classic"->{
                    correctAnswer = question.correctAnswers.random()
                }
                "spelling_correction"->{
                    correctAnswer = question.correctedSpelling
                }
            }
            val feedbackFragment = FeedbackBottomSheetFragment.newInstance(isCorrect, correctAnswer)
            feedbackFragment.setOnDismissListener(this)
            feedbackFragment.show(supportFragmentManager, "FeedbackBottomSheet")

        }
        else{
            var correctAnswer=""
            when(question.questionType){
                "classic"->{
                    correctAnswer = question.correctAnswers.random()
                    if(isCorrect){
                        totalScore+=2
                    }
                    possibleScore+=2

                }
                "multiple_choice"->{
                    correctAnswer = question.correctAnswerMC
                    if(isCorrect)
                        totalScore+=question.options.size*2
                    possibleScore+=question.options.size*2
                }
                "true_false"->{
                    if(isCorrect)
                        totalScore+=1
                    possibleScore+=1
                }
                "spelling_correction"->{
                    correctAnswer = question.correctedSpelling

                    if(isCorrect){
                        var score=Math.min(Math.max(question.correctedSpelling.length/15,2),5)
                        totalScore+=score
                        possibleScore+=score
                    }
                    else{
                        possibleScore+=2
                    }
                }
            }

            val feedbackFragment = FeedbackBottomSheetFragment.newInstance(isCorrect, correctAnswer)
            feedbackFragment.setOnDismissListener(this)
            feedbackFragment.show(supportFragmentManager, "FeedbackBottomSheet")
        }


    }
    override fun onDismissed() {
        currentQuestionIndex++
        showNextQuestion()
    }
    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.remove("AUTH_TOKEN")
        editor.apply()
        val intent = Intent(this@QuestionsActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
