package com.example.helps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.add_question.*

class AddActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_question)

        btnSubmit.setOnClickListener{
            submit()
        }
    }

    private fun submit()
    {
        val questionName:String = editTextQuestion.text.toString()
        val reply = " "
        val id ="1"

        if(TextUtils.isEmpty(editTextQuestion.text))
        {
            editTextQuestion.setError(getString(R.string.error_value_required))
            return
        }



        val intent = Intent()
        intent.putExtra(EXTRA_QUESTION, questionName)
        intent.putExtra(EXTRA_REPLY, reply)
        intent.putExtra(EXTRA_ID, id)

        setResult(Activity.RESULT_OK, intent)

        finish()
    }

    companion object{
        const val EXTRA_QUESTION = "com.example.helps.QUESTION"
        const val EXTRA_REPLY = "com.example.helps.REPLY"
        const val EXTRA_ID = "com.example.helps.ID"
    }
}