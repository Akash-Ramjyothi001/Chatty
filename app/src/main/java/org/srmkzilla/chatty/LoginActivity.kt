package org.srmkzilla.chatty

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import org.srmkzilla.chatty.viewmodel.AuthMethod
import org.srmkzilla.chatty.viewmodel.AuthViewModel


class LoginActivity : AppCompatActivity() {

    lateinit var viewmodel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewmodel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        viewmodel.init()
        viewmodel.user.observe(this@LoginActivity, Observer {
           if(it==null){
               return@Observer
           }
            else{
               val intent = Intent(this,ChatsActivity::class.java)
               startActivity(intent)
               finish()
           }
        })
        viewmodel.authMethod.observe(this@LoginActivity, Observer {
            if(it==null){
                return@Observer
            }
            else{
                updateUI(it)
            }
        })


    }



    fun updateUI(authMethod: AuthMethod){
        when(authMethod){
            AuthMethod.SIGNIN -> {showLogin()}
            AuthMethod.SIGNUP -> {showSignup()}
        }
    }

    fun showLogin(){
        editText3.visibility = View.INVISIBLE
        button.text = "SIGNIN"
        textView2.text = "Don't have an account? Signup instead"
        button.setOnClickListener {
            val email = editText.text.toString()
            val password = editText2.text.toString()
            viewmodel.login(email, password)
        }
        textView2.setOnClickListener { viewmodel.authMethod.postValue(AuthMethod.SIGNUP) }
    }
    fun showSignup(){
        editText3.visibility = View.VISIBLE
        button.text = "SIGNUP"
        textView2.text = "Already have an account? Signin instead"
        button.setOnClickListener {
            val email = editText.text.toString()
            val password = editText2.text.toString()
            val name = editText3.text.toString()
            viewmodel.signUp(email, password, name)
        }
        textView2.setOnClickListener { viewmodel.authMethod.postValue(AuthMethod.SIGNIN) }
    }
}
