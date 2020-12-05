package com.devozz.evodeme

//d5d309701918227880838d280c4c94536c5789e0
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.ImageDecoder.createSource
import android.graphics.ImageDecoder.decodeBitmap
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.devozz.evodeme.utils.Constants.DB_FIELD_COMMENT
import com.devozz.evodeme.utils.Constants.DB_FIELD_DATE
import com.devozz.evodeme.utils.Constants.DB_FIELD_DOWNLOADURL
import com.devozz.evodeme.utils.Constants.DB_FIELD_USEREMAIL
import com.devozz.evodeme.utils.Constants.FIREBASE_COLLECTION_PATH
import com.devozz.evodeme.utils.Constants.FIREBASE_IMAGES_PATH
import com.devozz.evodeme.utils.toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID.randomUUID

class UploadActivity : AppCompatActivity() {

    var selectedPicture: Uri? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    private lateinit var uploadImageView: ImageView
    private lateinit var uploadCommentText: EditText
    private lateinit var btnUpload: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        initUI()
        initListeners()
        initFirebase()
    }

    private fun initListeners() {
        btnUpload.setOnClickListener { uploadOperation() }
    }

    private fun initUI() {
        uploadImageView = findViewById(R.id.uploadImageView)
        uploadCommentText = findViewById(R.id.uploadCommentText)
        btnUpload = findViewById(R.id.btnUpload)
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
    }


    fun imageViewClicked(view: View) {

        if (ContextCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE
            ) != PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            Intent(ACTION_PICK, EXTERNAL_CONTENT_URI).also {
                startActivityForResult(it, 2)

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
            Intent(ACTION_PICK, EXTERNAL_CONTENT_URI).also {
                startActivityForResult(it, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPicture = data.data
            try {
                selectedPicture?.let {
                    when {
                        SDK_INT >= 28 -> {
                            createSource(contentResolver, it).let { source ->
                                decodeBitmap(source).also { bitmap ->
                                    uploadImageView.setImageBitmap(bitmap)
                                }
                            }
                        }
                        else -> getBitmap(
                            contentResolver,
                            selectedPicture
                        ).run { uploadImageView.setImageBitmap(this) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadOperation() {
        selectedPicture?.let { image ->
            storage.reference
                .child(FIREBASE_IMAGES_PATH)
                .child("${randomUUID()}.jpg")
                .putFile(image)
                .addOnSuccessListener {
                    it.storage.downloadUrl.let { uri ->
                        hashMapOf<String, Any>().apply {
                            put(DB_FIELD_DOWNLOADURL, uri.toString())
                            put(DB_FIELD_USEREMAIL, auth.currentUser?.email.toString())
                            put(DB_FIELD_COMMENT, uploadCommentText.text.toString())
                            put(DB_FIELD_DATE, Timestamp.now())
                        }.also {
                            db.collection(FIREBASE_COLLECTION_PATH)
                                .add(it)
                                .addOnCompleteListener { task ->
                                    if (task.isComplete && task.isSuccessful) finish()
                                }.addOnFailureListener { exception ->
                                    toast(exception.message.toString())
                                }
                        }
                    }
                }
        }
    }
}