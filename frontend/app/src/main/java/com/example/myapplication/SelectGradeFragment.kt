package com.example.myapplication

import android.app.FragmentManager
import android.content.Context
import android.content.SharedPreferences
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.myapplication.activities.MainActivity
import com.example.myapplication.models.Child
import com.example.myapplication.models.MessageResult
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SelectGradeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectGradeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sharedPreferences:SharedPreferences
    private var retrofitInterface=RetrofitInstance.retrofitInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        sharedPreferences=requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_select_grade, container, false)
        val firstGrade=view.findViewById<ImageView>(R.id.srcFirstGrade)
        val secondGrade=view.findViewById<ImageView>(R.id.srcSecondGrade)
        val thirdGrade=view.findViewById<ImageView>(R.id.srcThirdGrade)
        val forthGrade=view.findViewById<ImageView>(R.id.srcForthGrade)
        firstGrade.setOnClickListener{
            setGrade(1)
        }
        secondGrade.setOnClickListener{
            setGrade(2)
        }
        thirdGrade.setOnClickListener{
            setGrade(3)
        }
        forthGrade.setOnClickListener{
            setGrade(4)
        }
        return view
    }
    private fun setGrade(grade: Int) {
        val tok = sharedPreferences.getString("AUTH_TOKEN", null)
        val token = "Bearer $tok"
        val childId = getChildFromSharedPreference()?._id

        val map = HashMap<String, Int>()
        map["selectedGrade"] = grade
        val gson = Gson()
        val childJson = sharedPreferences.getString("child", null)
        var child: Child? = null
        if (childJson != null) {
            child = gson.fromJson(childJson, Child::class.java)
        }
        val childJson2 = gson.toJson(child?.let { Child(it._id,child.name,null,child.image,child.progress,grade) })

        with(sharedPreferences.edit()) {
            putString("child", childJson2)
            apply()
        }
        val call = retrofitInterface.updateChildGrade(token, childId?:"", map)

        call.enqueue(object : retrofit2.Callback<MessageResult> {
            override fun onResponse(call: Call<MessageResult>, response: Response<MessageResult>) {
                if (response.isSuccessful) {
                    (requireContext() as MainActivity).enableLessons()
                    (requireContext() as MainActivity).supportFragmentManager.popBackStack(null, 0)
                    (requireContext() as MainActivity).setCurrentFragment(LessonsFragment())                }
                else{
                    Log.d("Moje poruke neuspesno",response.code().toString())
                }
            }

            override fun onFailure(call: Call<MessageResult>, t: Throwable) {
                Log.d("Moje poruke neuspesno",t.toString())

            }
        })
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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SelectGradeFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SelectGradeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}