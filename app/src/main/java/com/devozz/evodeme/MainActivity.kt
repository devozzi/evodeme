package com.devozz.evodeme

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.devozz.evodeme.utils.toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFirebaseInstance()
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        auth.currentUser?.let {
            startFeedActivity()
        }
    }

    private fun startFeedActivity() =
        Intent(applicationContext, FeedActivity::class.java).run {
            startActivity(this)
            finish()
        }

    private fun initFirebaseInstance() {
        auth = FirebaseAuth.getInstance()
    }


    fun signInClicked(view: View) {
        auth.signInWithEmailAndPassword(
                userEmailText.text.toString(),
                passwordText.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Signed In
                        toast("Welcome ${auth.currentUser?.email.toString()}")
                        startFeedActivity()
                    }
                }.addOnFailureListener { e ->
                    toast(e.message.toString())
                }

    }

    fun signUpClicked(view: View) {
        auth.createUserWithEmailAndPassword(
            userEmailText.text.toString(),
            passwordText.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful)
                startFeedActivity()
        }.addOnFailureListener { e ->
            toast(e.message.toString())
        }
    }
}