package com.example.myapplication

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.media.MediaPlayer

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

fun playCorrectSound(context: Context) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.sound_correct)
    mediaPlayer.start()
}

fun playWrongSound(context: Context) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.sound_incorrect)
    mediaPlayer.start()
}

class FeedbackBottomSheetFragment : BottomSheetDialogFragment() {

    interface OnDismissListener {
        fun onDismissed()
    }

    private var isCorrectAnswer: Boolean = false
    private var correctAnswer: String = ""
    private var dismissListener: OnDismissListener? = null
    private val positiveFeedback: List<String> = listOf(
        "Браво! Одговор је тачан. Само тако настави!",
        "Одлично! Показујеш си да одлично знаш!",
        "Тачан одговор! Настави да радиш тако вредно!",
        "Сјајно! Одговор је тачан!",
        "Феноменално! Одговор је тачан!",
        "Браво за тачан одговор!",
        "Одговор је исправан! Види се да вредно учиш!",
        "Свака част на тачном одговору! Настави да радиш тако успешно!",
        "Супер! Свака част на тачном одговору!",
        "Одлично! Види се да се трудиш!",
        "Фантастично! Само тако настави!",
        "Браво, сјајан посао! Тачан одговор!",
        "Изванредно! Тако се то ради!",
        )
    private val constructiveFeedback: List<String> = listOf(
        "Одговор није тачан, али нема везе! Сви понекад грешимо, важно је да наставиш да учиш!",
        "Нажалост, ово није тачно. Добар покушај! Следећи пут ћеш бити успешнији!",
        "Ово није тачан одговор, али грешке су део учења. Покушај поново и успећеш!",
        "Одговор није тачан, али одличан труд! Сваки покушај те приближава успеху!",
        "Овај одговор није исправан, али браво за труд! Настави да вежбаш, успећеш ускоро!",
        "Није тачно, али не одустај! Следећи пут ће бити боље!",
        "Нажалост, ово није исправно, али само полако, труд се увек исплати!",
        "Ово није тачан одговор, али и ово је корак напред! Учи се из сваке грешке.",
        "Одговор није исправан, али не бринеш, следећи пут ће бити лакше! Ти то можеш!"
    )
    companion object {
        fun newInstance(isCorrect: Boolean, correctAnswer: String): FeedbackBottomSheetFragment {
            val fragment = FeedbackBottomSheetFragment()
            val args = Bundle().apply {
                putBoolean("isCorrect", isCorrect)
                putString("correctAnswer", correctAnswer)
            }
            fragment.arguments = args
            return fragment
        }
    }

    fun setOnDismissListener(listener: OnDismissListener) {
        this.dismissListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isCorrectAnswer = it.getBoolean("isCorrect")
            correctAnswer = it.getString("correctAnswer").orEmpty()
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

        if (isCorrectAnswer) {
            playCorrectSound(requireContext())
            feedbackTextView.text = positiveFeedback.random()
            view.setBackgroundColor(Color.GREEN)
        } else {
            playWrongSound(requireContext())
            if(correctAnswer!="")
            feedbackTextView.text = constructiveFeedback.random().toString()+"\n Тачан одговор је $correctAnswer."
            else{
                feedbackTextView.text = constructiveFeedback.random().toString()
            }
            view.setBackgroundColor(Color.RED)
        }

        nextButton.setOnClickListener {
            dismiss()

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismissed()
    }
}

