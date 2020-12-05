package com.devozz.evodeme

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.devozz.evodeme.utils.Constants.DB_FIELD_COMMENT
import com.devozz.evodeme.utils.Constants.DB_FIELD_DATE
import com.devozz.evodeme.utils.Constants.DB_FIELD_DOWNLOADURL
import com.devozz.evodeme.utils.Constants.DB_FIELD_USEREMAIL
import com.devozz.evodeme.utils.Constants.FIREBASE_COLLECTION_PATH
import com.devozz.evodeme.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import com.google.firebase.firestore.QuerySnapshot

class FeedActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView

    var userEmailFromFB = ArrayList<String>()
    var userCommentFromFB = ArrayList<String>()
    var userImageFromFB = ArrayList<String>()

    var feedAdapter: FeedRecyclerAdapter? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_post -> { //Upload Activity
                Intent(applicationContext, UploadActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.logout -> { //Logout
                auth.signOut()
                Intent(applicationContext, MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        initUI()
        initFirebase()
        getDataFromFirestore()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FeedActivity, VERTICAL, false)
            feedAdapter = FeedRecyclerAdapter(userEmailFromFB, userCommentFromFB, userImageFromFB)
            recyclerView.adapter = adapter
        }
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun initUI() {
        recyclerView = findViewById(R.id.recyclerView)
    }


    private fun getDataFromFirestore() {
        db.collection(FIREBASE_COLLECTION_PATH)
            .orderBy(DB_FIELD_DATE, DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                snapshot?.run { //NotNull
                    updateLocalData()
                } ?: kotlin.run { //Null
                    toast(exception?.message.toString())
                }
            }
    }

    private fun QuerySnapshot.updateLocalData() {
        if (!documents.isNullOrEmpty()) {
            clearLocalData()
            documents.forEach { documentItem ->
                with(documentItem) {
                    getString(DB_FIELD_COMMENT)?.let { userCommentFromFB.add(it) }
                    getString(DB_FIELD_USEREMAIL)?.let { userEmailFromFB.add(it) }
                    getString(DB_FIELD_DOWNLOADURL)?.let { userImageFromFB.add(it) }
                    getTimestamp(DB_FIELD_DATE)?.let { }//Todo
                }
            }
        }
    }

    private fun clearLocalData() {
        userImageFromFB.clear()
        userCommentFromFB.clear()
        userEmailFromFB.clear()
    }
}
