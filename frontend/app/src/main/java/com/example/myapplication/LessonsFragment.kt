package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.activities.SelectChildActivity
import com.example.myapplication.adapters.TopicAdapter
import com.example.myapplication.models.Child
import com.example.myapplication.models.LoginResult
import com.example.myapplication.models.Topic
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LessonsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LessonsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var retrofitInterface=RetrofitInstance.retrofitInterface
    private lateinit var  sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        sharedPreferences=requireContext().getSharedPreferences("user_session",Context.MODE_PRIVATE)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_lessons, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gson = Gson()
        val childJson = sharedPreferences.getString("child", null)
        var child: Child? = null
        if (childJson != null) {
            child = gson.fromJson(childJson, Child::class.java)
        }
        val token = sharedPreferences.getString("AUTH_TOKEN", null)
        val call: Call<List<Topic>> = retrofitInterface.getTopics("Bearer $token", child?.selectedGrade ?: 0)
        call.enqueue(object : Callback<List<Topic>> {
            override fun onResponse(call: Call<List<Topic>>, response: Response<List<Topic>>) {
                if (response.code() == 200) {
                    if (isAdded) {
                        val topics = response.body()
                        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewLessonFragment)
                        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
                        recyclerView?.setHasFixedSize(true)

                        if (topics != null) {
                            recyclerView?.adapter = TopicAdapter(requireContext(), topics.sortedBy { it.position })
                        }
                    }
                }
            }



            override fun onFailure(call: Call<List<Topic>>, t: Throwable) {

            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LessonsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LessonsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}