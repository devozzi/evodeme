package com.devozz.evodeme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.devozz.evodeme.utils.toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnSignIn: Button
    private lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        initListeners()
        initFirebaseInstance()
        checkCurrentUser()
    }

    private fun initListeners() {
        btnSignIn.setOnClickListener { signInOperation() }
        btnSignUp.setOnClickListener { signUpOperation() }
    }

    private fun signInOperation() {
        auth.signInWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Signed In
                toast("Welcome ${auth.currentUser?.email.toString()}")
                startFeedActivity()
            }
        }.addOnFailureListener { e ->
            toast(e.message.toString())
        }
    }

    private fun signUpOperation() {
        auth.createUserWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful)
                startFeedActivity()
        }.addOnFailureListener { e ->
            toast(e.message.toString())
        }
    }

    private fun initUI() {
        email = findViewById(R.id.edtEmail)
        password = findViewById(R.id.edtPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnSignUp = findViewById(R.id.btnSignUp)
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
}