package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.myapplication.activities.LoginActivity
import com.example.myapplication.activities.SelectChildActivity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_settings, container, false)
        val btnLogout=view.findViewById<Button>(R.id.btnLogout)
        val btnChangeChild=view.findViewById<Button>(R.id.btnChangeChild)
        btnLogout.setOnClickListener{
            logout()
        }
        btnChangeChild.setOnClickListener{
            val intent = Intent(requireContext(), SelectChildActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
//            requireActivity().finish()
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun logout() {
        val editor = sharedPreferences.edit()
        //editor.remove("AUTH_TOKEN")
        editor.clear()
        editor.apply()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}