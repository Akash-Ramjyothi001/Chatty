package org.srmkzilla.chatty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chats.*
import java.util.*

class ChatsActivity : AppCompatActivity() {

    class MessageViewHolder(v: View): RecyclerView.ViewHolder(v) {
        internal var nameTextView: TextView
        internal var timeTextView:TextView
        internal var messageTextView: TextView

        init {
            nameTextView = itemView.findViewById(R.id.textView)
            timeTextView = itemView.findViewById(R.id.textView3)
            messageTextView = itemView.findViewById(R.id.textView4)
        }
    }

    lateinit var mAuth: FirebaseAuth
    lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<ChattyMessage,MessageViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)
        mAuth = FirebaseAuth.getInstance()
        val mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = mLinearLayoutManager

        val mDatabaseReference = FirebaseDatabase.getInstance().reference
        val parser = SnapshotParser { datasnapshot ->
            val chattyMessage = datasnapshot.getValue(ChattyMessage::class.java)
            chattyMessage?.id = datasnapshot.key
            chattyMessage ?:ChattyMessage()
        }
         val messageRef = mDatabaseReference.child("messages")
        val options = FirebaseRecyclerOptions.Builder<ChattyMessage>().setQuery(messageRef, parser).build()
        mFirebaseAdapter = object: FirebaseRecyclerAdapter<ChattyMessage, MessageViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
                val inflator = LayoutInflater.from(parent.context)
                return MessageViewHolder(inflator.inflate(R.layout.chat_item, parent, false))
            }

            override fun onBindViewHolder(viewholder: MessageViewHolder, position: Int, chattyMessage: ChattyMessage){
                viewholder.nameTextView.text = chattyMessage.name
                viewholder.messageTextView.text = chattyMessage.message
                viewholder.timeTextView.text = Date(chattyMessage.time).toString()
            }
        }

        recyclerView.adapter = mFirebaseAdapter
        button2.setOnClickListener {
            val chattyMessage = ChattyMessage(
                message = editText4.text.toString(),
                name = mAuth.currentUser?.displayName!!,
                time = Date().time
            )
            mDatabaseReference.child("messages").push().setValue(chattyMessage)
            editText4.setText("")
        }
    }

    public override fun onStart() {
        super.onStart()
        mFirebaseAdapter.startListening()
    }

    public override fun onPause(){
        mFirebaseAdapter.stopListening()
        super.onPause()
    }


    data class ChattyMessage(var id:String? = null, var name:String, var message:String, var time:Long){
        constructor(name: String, message: String): this(null,name, message, 0L)
        constructor(name:String, message: String, time:Long): this(null, name, message, time)
        constructor(): this(null,"Anonymous","",0L)
    }

}
