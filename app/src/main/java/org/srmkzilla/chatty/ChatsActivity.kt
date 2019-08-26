package org.srmkzilla.chatty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_chats.*
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.FirebaseDatabase
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import android.view.*


class ChatsActivity : AppCompatActivity() {

    class MessageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var userNameTextView: TextView
        internal var messageTextView: TextView
        internal var timestampTextView: TextView
        internal var card: CardView

        init {
            userNameTextView = itemView.findViewById(R.id.name) as TextView
            messageTextView = itemView.findViewById(R.id.message) as TextView
            timestampTextView = itemView.findViewById(R.id.time) as TextView
            card = itemView.findViewById(R.id.cardView) as CardView
        }
    }

    lateinit var mAuth: FirebaseAuth
    lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<ChattyMessage, MessageViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        mAuth = FirebaseAuth.getInstance()
        val mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = mLinearLayoutManager

        val mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        val parser = SnapshotParser{ dataSnapshot ->
            val chattyMessage = dataSnapshot.getValue(ChattyMessage::class.java)
            chattyMessage?.id = dataSnapshot.key.toString()
            chattyMessage ?: ChattyMessage(null,"","",0L)
        }

        val messagesRef = mFirebaseDatabaseReference.child("messages").orderByChild("timestamp")
        val options = FirebaseRecyclerOptions.Builder<ChattyMessage>()
            .setQuery(messagesRef, parser)
            .build()
        mFirebaseAdapter = object : FirebaseRecyclerAdapter<ChattyMessage, MessageViewHolder>(options) {
            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MessageViewHolder {
                val inflater = LayoutInflater.from(viewGroup.context)
                return MessageViewHolder(inflater.inflate(R.layout.chat_item, viewGroup, false))
            }
            override fun onBindViewHolder(
                viewHolder: MessageViewHolder,
                position: Int,
                chattyMessage: ChattyMessage
            ) {
                viewHolder.userNameTextView.text = chattyMessage.userName
                viewHolder.messageTextView.text = chattyMessage.message
                viewHolder.timestampTextView.text = Date(chattyMessage.timestamp!!).toString()

                val params = FrameLayout.LayoutParams(viewHolder.card.layoutParams)
                if(chattyMessage.senderId == mAuth.currentUser?.uid){
                    params.gravity = GravityCompat.END
                    Log.d("gravity","end")
                }
                else{
                    params.gravity = GravityCompat.START
                    Log.d("gravity", "start")
                }
                viewHolder.card.layoutParams = params
                Log.d("gravity","set")

            }
        }
        mFirebaseAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val friendlyMessageCount = mFirebaseAdapter.itemCount
                val lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (lastVisiblePosition == -1 || positionStart >= friendlyMessageCount - 1 && lastVisiblePosition == positionStart - 1) {
                    recyclerView.scrollToPosition(positionStart)
                }
            }
        })

        recyclerView.adapter = mFirebaseAdapter

        editText4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                button2.isEnabled = charSequence.toString().trim { it <= ' ' }.isNotEmpty()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        button2.setOnClickListener {
            val chattyMessage = ChattyMessage(
                message = editText4.text.toString(),
                userName =  mAuth.currentUser?.displayName,
                timestamp = Date().time,
                senderId = mAuth.currentUser?.uid)

            mFirebaseDatabaseReference.child("messages")
                .push().setValue(chattyMessage)
            editText4.setText("")
        }

    }

    public override fun onPause() {
        mFirebaseAdapter.stopListening()
        super.onPause()
    }

    public override fun onStart() {
        super.onStart()
        mFirebaseAdapter.startListening()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signout -> {
                logout(); true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    fun logout(){
        mAuth.signOut()
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    data class ChattyMessage(var id:String? = null, var userName:String? ="Anonymous", var message:String, var timestamp:Long? = null, var senderId:String?=""){
        constructor(userName: String?,message: String, senderId: String): this(null, userName, message, 0L, senderId)

        constructor(userName: String?,message: String, timestamp: Long?, senderId:String): this(null, userName, message, timestamp, senderId)

        constructor():this(null,"Anonymous","",0L,"")
    }


}
