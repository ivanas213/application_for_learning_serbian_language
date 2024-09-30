package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.myapplication.activities.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomDialogFragmentTest: BottomSheetDialogFragment() {
    private var score:Int=0
    private val feedback40:List<String> =listOf(
        "Није лоше, али можеш ти то боље! Следећи пут ћеш то показати.",
        "Вежбање је кључ! Пробај поново и сигурно бити још боље!",
        "Сви праве грешке, али важно је да се трудимо даље! Следећи пут ћеш сигурно успети!",
        "Сваки пут научимо нешто ново. Настави да радиш и резултат ће бити бољи!")
    private val feedback60: List<String> = listOf(
        "На правом си путу! Само још мало труда и биће сјајно!",
        "Одлично! Само напред, са још мало вежбе постајеш прави стручњак!",
        "Браво! Још мало вежбе и бићеш на врху!"
    )
    private val feedback80: List<String> = listOf(
        "Браво, радиш супер посао! Настави тако!",
        "Одлично! Још само корак до савршенства!",
        "Твој труд се исплатио! Само напред!",
        "Фантастично! Покажи нам колико још можеш да напредујеш!"
    )
    private val feedback90:List<String> = listOf(
        "Сјајно! Скоро си на врху, само још мало!",
        "Вау! Фантастично! Мало фали да постанеш експерт!",
        "Тако близу! Још мало труда и освојићеш све!",
        "Одлично! Следећи пут идемо на 100%!"
    )
    private val feedback100:List<String> =listOf(
        "Браво! Ти си прави мајстор у овоме!",
        "Фантастичан резултат! Настави тако и освојићеш све!",
        "Суперхерој! Поносни смо на тебе!"
    )
    companion object {
        fun newInstance(score:Int): BottomDialogFragmentTest {
            val fragment = BottomDialogFragmentTest()
            val args = Bundle().apply {
                putInt("score", score)
            }
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            score = it.getInt("score")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_feedback_bottom_sheet, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        val feedbackTextView = view.findViewById<TextView>(R.id.feedbackMessage)
        val nextButton = view.findViewById<Button>(R.id.nextButton)
        val feedbackMessage="Твој резултат је $score.\n"
        if(score<40){
            feedbackTextView.text=feedbackMessage+feedback40.random()
            playWrongSound(requireContext())
            view.setBackgroundColor(Color.parseColor("#FF0000"))
        }
        else{
            playCorrectSound(requireContext())
            view.setBackgroundColor(Color.parseColor("#00FF00"))
            if(score<60){
                feedbackTextView.text=feedbackMessage+feedback60.random()
            }
            else if(score<80){
                feedbackTextView.text=feedbackMessage+feedback80.random()
            }
            else if(score<90){
                feedbackTextView.text=feedbackMessage+feedback90.random()
            }
            else{
                feedbackTextView.text=feedbackMessage+feedback100.random()
            }
        }

        nextButton.setOnClickListener {
            dismiss()
            Intent(requireContext(), MainActivity::class.java).also {
                startActivity(it)
                (requireContext() as Activity).finish()
            }
        }
    }
    private fun playCorrectSound(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.sound_correct)
        mediaPlayer.start()
    }

    private fun playWrongSound(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.sound_incorrect)
        mediaPlayer.start()
    }
}