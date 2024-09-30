package com.example.myapplication

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.myapplication.activities.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFeedbackHangman : BottomSheetDialogFragment() {
    private var dismissListener: com.example.myapplication.FeedbackBottomSheetFragment.OnDismissListener? = null
    private var isCorrect: Boolean = false
    private var correctAnswer:String=""

    companion object {
        fun newInstance(isCorrect: Boolean, correctAnswer:String): BottomFeedbackHangman {
            val fragment = BottomFeedbackHangman()
            val args = Bundle().apply {
                putBoolean("isCorrect", isCorrect)
                putString("correctAnswer",correctAnswer)
            }
            fragment.arguments = args
            return fragment
        }
    }
    fun setOnDismissListener(listener: com.example.myapplication.FeedbackBottomSheetFragment.OnDismissListener) {
        this.dismissListener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_feedback_hangman, container, false)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isCorrect = it.getBoolean("isCorrect")
            correctAnswer= it.getString("correctAnswer").toString()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        val feedbackTextView = view.findViewById<TextView>(R.id.feedbackMessage)
        val againButton = view.findViewById<Button>(R.id.againButton)
        val backButton=view.findViewById<Button>(R.id.backButton)

        if (isCorrect) {
            playCorrectSound(requireContext())
            feedbackTextView.text = "Браво! Реч је тачна"
            view.setBackgroundColor(Color.GREEN)
        } else {
            playWrongSound(requireContext())
            feedbackTextView.text = "Тачна реч је $correctAnswer"
            view.setBackgroundColor(Color.RED)
        }

        againButton.setOnClickListener {
            dismiss()
            (requireContext() as Activity).recreate()
        }
        backButton.setOnClickListener {
            Intent(requireContext(),MainActivity::class.java).also{
                startActivity(it)
                (requireContext() as Activity).finish()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismissed()
    }
}