package com.example.helps

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_help.*
import java.lang.Exception
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject

class HelpMain : AppCompatActivity() {
    private val REQUEST_CODE = 1
    private lateinit var progress: ProgressBar
    private lateinit var questionList: ArrayList<Questions>
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_help)

        //initialise variables and ui
        questionList = ArrayList<Questions>()

        adapter = Adapter(this)
        adapter.setPost(questionList)

        progress = findViewById(R.id.progress_bar)
        progress.visibility = View.GONE

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener { view->
            val intent = Intent(this, AddActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
         //Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         //Handle action bar item clicks here. The action bar will
         //automatically handle clicks on the Home/Up button, so long
         //as you specify a parent activity in AndroidManifest.xml.

        return when(item.itemId) {
            R.id.action_sync -> {
                syncQuestion()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                data?.let {
                    val help = Questions(it.getStringExtra(AddActivity.EXTRA_QUESTION), it.getStringExtra(AddActivity.EXTRA_REPLY), it.getStringExtra(AddActivity.EXTRA_ID))
                    createQuestion(help)
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun createQuestion(ques: Questions)
    {
        val url = getString(R.string.url_server) + getString(R.string.url_user_create) + "?question=" + ques.questionName + "&reply=%20" + "&id=1"

        progress.visibility = View.VISIBLE

        val jsonObjectRequest = JsonObjectRequest (

            Request.Method.GET, url, null,
            Response.Listener { response ->
                //process the JSON
                try{
                    if(response != null)
                    {
                        val strResponse = response.toString()
                        val jsonResponse  = JSONObject(strResponse)
                        val success: String = jsonResponse.get("success").toString()

                        if(success.equals("1"))
                        {
                            Toast.makeText(applicationContext, "Submit Successful!", Toast.LENGTH_SHORT).show()
                            //Add question to question list
                            questionList.add(ques)

                        }
                        else
                        {
                            Toast.makeText(applicationContext, "Fail to Submit!", Toast.LENGTH_SHORT).show()
                        }
                        progress.visibility = View.GONE
                    }
                }catch (e:Exception)
                {
                    Log.d("Main", "Response: %s".format(e.message.toString()))
                    progress.visibility = View.GONE
                }
            },
            Response.ErrorListener { error ->
                Log.i("Main", "Response: %s".format(error.message.toString())).toString()
                Log.d("Main", "Response: %s".format(error.message.toString())).toString()
                progress.visibility = View.GONE
            }
        )

        //volley request policy, only one time request
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            0, //no retry
            1f
        )

        //Access the requestQueue through your singleton class
        helpSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    private fun syncQuestion()
    {
        val url = getString(R.string.url_server) + getString(R.string.url_user_read)

        //display progress bar
        progress.visibility = View.VISIBLE
        //delete all user records
        questionList.clear()

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener{ response ->
                //process the JSON
                try{
                    if(response != null)
                    {
                        val strResponse = response.toString()
                        val jsonResponse = JSONObject(strResponse)
                        val jsonArray: JSONArray = jsonResponse.getJSONArray("records")

                        val size: Int = jsonArray.length()

                        for(i in 0..size-1)
                        {
                            var jsonQuestion: JSONObject = jsonArray.getJSONObject(i)
                            var help: Questions = Questions(jsonQuestion.getString("question"),
                                jsonQuestion.getString("reply"),
                                jsonQuestion.getString("id"))

                            questionList.add(help)
                        }
                        progress.visibility = View.GONE
                    }
                }catch (e:Exception)
                {
                    Log.d("Main", "Response: %s".format(e.message.toString()))
                    progress.visibility = View.GONE
                }
            },
            Response.ErrorListener { error ->
                Log.d("Main", "Response: %s".format(error.message.toString()))
                progress.visibility = View.GONE
            }
        )
        // Volley request policy, only one time request
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            0, //no retry
            1f
        )

        //access the RequestQueue through your singleton class
        helpSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }

}
