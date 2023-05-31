package com.smartcompany.googletest

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseHelper: FirebaseHelper
    var database = FirebaseDatabase.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseHelper = FirebaseHelper(this)

    }

    fun signInGoogle(view: View) {
        firebaseHelper.getoneTapClient().beginSignIn(firebaseHelper.getsignUpRequest())
            .addOnSuccessListener(this) { result ->
                try {
                    val intentSender = result.pendingIntent.intentSender
                    someActivityResultLauncher.launch(
                        IntentSenderRequest.Builder(intentSender).build()
                    )

                } catch (e: IntentSender.SendIntentException) {
                    Log.e("onTap", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
                Log.d("onTap", e.localizedMessage)
            }


    }


    private val someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val credential = firebaseHelper.getoneTapClient().getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                val googleAuthCredential = GoogleAuthProvider.getCredential(idToken, null)

                if (idToken != null) {
                    firebaseAuth.signInWithCredential(googleAuthCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val currentUser = firebaseAuth.currentUser
                                val oneTapUser = task.result?.user
                                Log.i("onTap", "User: ${oneTapUser?.uid}")

                                if (oneTapUser?.uid != null) {
                                    val userRef =
                                        database.reference.child("users").child(oneTapUser.uid)
                                    userRef.addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Log.i("onTap", "User already exists")
                                            } else {
                                                val userDataBase = User()
                                                userDataBase.id = oneTapUser.uid
                                                userDataBase.avatarUser = oneTapUser.photoUrl.toString()
                                                userDataBase.email = oneTapUser.email
                                                userDataBase.name = oneTapUser.displayName
                                                userDataBase.phoneNumber = oneTapUser.phoneNumber
                                                userDataBase.createdAt =
                                                    LocalDateTime.now().toString()
                                                userDataBase.saveUser(userDataBase)
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            // Handle database error
                                        }
                                    })
                                }

                                startActivity(Intent(this, SecondActivity::class.java))
                                finish()
                            } else {
                                // Trate a falha na autenticação, se necessário
                            }
                        }
                }

            }
        }
}
