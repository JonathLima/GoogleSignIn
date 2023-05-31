package com.smartcompany.googletest

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Error

class User {

    private lateinit var database: DatabaseReference

    var id: String = ""
    var name: String? = ""
    var email: String? = ""
    @Exclude
    var password: String? = null
    var createdAt: String? = null
    var avatarUser: String? = null
    var phoneNumber: String? = null

    fun saveUser(user: User) {
        try{
            database = Firebase.database.reference
            database.child("users").child(user.id).setValue(user)

        }catch(e: Error){
            Log.i("onTap", "Error: $e")
        }

    }

    fun getUser(id: String): DatabaseReference {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        return database.reference.child("users").child(id)
    }


}