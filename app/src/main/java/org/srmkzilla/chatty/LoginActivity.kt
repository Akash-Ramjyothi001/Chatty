package org.srmkzilla.chatty

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import org.srmkzilla.chatty.viewmodel.AuthViewModel


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val viewmodel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
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
//        button.setOnClickListener {
//            val email = editText.text.toString()
//            val password = editText2.text.toString()
//            signUp(email, password)
//        }
    }

//    public override fun onStart() {
//        super.onStart()
//        val currentUser = mAuth.currentUser
//        updateUI(currentUser)
//    }


    fun updateUI(user:FirebaseUser?){
        if(user != null ){
            val intent = Intent(this,ChatsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
