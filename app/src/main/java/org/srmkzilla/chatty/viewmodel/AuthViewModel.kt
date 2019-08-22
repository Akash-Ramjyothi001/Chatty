package org.srmkzilla.chatty.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application):AndroidViewModel(application) {
    val user = MutableLiveData<FirebaseUser>()
    val authMethod = MutableLiveData<AuthMethod>()
    lateinit var mAuth: FirebaseAuth

    fun init(){
        mAuth = FirebaseAuth.getInstance()
        user.postValue(mAuth.currentUser)
        authMethod.postValue(AuthMethod.SIGNIN)
    }
    fun signUp(email:String, password:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.postValue(mAuth.currentUser)
                } else {
                    user.postValue(null)
                }

                // ...
            }
    }
    fun login(email:String, password:String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.postValue(mAuth.currentUser)
                } else {
                    user.postValue(null)
                }

                // ...
            }
    }
}

enum class AuthMethod {
    SIGNIN,
    SIGNUP
}