package com.smartcompany.googletest

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.values
import java.text.SimpleDateFormat
import java.util.Locale

class SecondActivity : AppCompatActivity() {
    private lateinit var avatarUser_img: ImageView;
    private lateinit var text_username: TextView;
    private lateinit var text_email: TextView;
    private lateinit var text_created_at: TextView;
    private lateinit var btn_signOut: Button;
    private val user: User = User();
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        startComponents();
        getUserData();
    }

    private fun getUserData(){
        val databaseUserId = firebaseAuth.currentUser?.uid.toString();
        val userData = user.getUser(databaseUserId);
        Log.i("onTap", "Dados: " + userData.child("email").get().toString())

        userData.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val email_database = snapshot.child("email").value as String
                    val name_database = snapshot.child("name").value as String
                    val avatarUser = snapshot.child("avatarUser").value as String
                    val created_at_database = snapshot.child("createdAt").value as String

                    showImage(avatarUser, avatarUser_img)
                    text_username.text = name_database
                    text_email.text = email_database;
                    text_created_at.text = "Criado em: ${formatData(created_at_database)}"

                } else {

                    Log.i("onTap", "Usuario não encontrado!");
                    // Tratar o caso em que o ID não corresponde a nenhum usuário
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    fun signOut(view: View) {
        firebaseAuth.signOut()
        val intent = Intent(view.context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        view.context.startActivity(intent)
        finish();
    }

    private fun startComponents() {
        avatarUser_img = findViewById(R.id.avatar_user);
        text_username = findViewById(R.id.name_user);
        text_email = findViewById(R.id.email_user);
        text_created_at = findViewById(R.id.created_at_user);
        btn_signOut = findViewById(R.id.btn_sign_out);

    }

    private fun showImage(imageString: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(imageString)
            .apply(RequestOptions().transform(CircleCrop()))
            .into(imageView)
    }

    private fun formatData(dataString: String): String {
        val formatIn = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val formatOut = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val data = formatIn.parse(dataString)
        return formatOut.format(data)
    }
}