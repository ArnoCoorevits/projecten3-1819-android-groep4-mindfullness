package com.groep4.mindfulness.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.groep4.mindfulness.R
import com.groep4.mindfulness.activities.MainActivity
import com.groep4.mindfulness.model.Message
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class FragmentChat : Fragment(){

    //Layout objecten
    private var listView: ListView? = null
    private var btnSend: View? = null
    private var editText: EditText? = null
    //Firebase objecten
    private var adapter: ArrayAdapter<String>? = null
    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var currentUserId: FirebaseUser? = FirebaseAuth.getInstance().currentUser!!
    private var messages : MutableList<Message> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // Top bar info instellen
        view.tr_page.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorGreen))
        view.tv_page.setText(R.string.contact)

        view.contactUser.text = "Psycholoog"

        btnSend = view.findViewById(R.id.btn_chat_send)

        displayChatMessages(view)

        /** Bericht sturen naar de database met als sleutel het userid van de huidig ingelogde gebruiker. */
        btnSend!!.setOnClickListener {
            editText = view.findViewById(R.id.msg_type)
            val naam = (activity as MainActivity)!!.gebruiker.name
            var message = Message()
            message.content = editText!!.text.toString()
            message.gelezen = false
            message.messageTime = Date().time
            message.messageUser = naam.toString()
            //var map = mapOf("messages" to messagesFirebase)
            Log.d("HEEEEEEEEEEEEEE", currentUserId!!.email!!.toString())
            firestore!!.collection("Chat").document(currentUserId!!.email!!)
                    .collection("messages").document((this.messages.size+1).toString()).set(message)
            editText!!.setText("")
            displayChatMessages(view)
        }

        return view
    }

    /**
     * De listAdapter zorgt ervoor dat de List opgevuld raakt met chatberichten opgehaald uit de database met de huidige gebruiker's userid als argument.
     */
    private fun displayChatMessages(view: View) {

        listView = view.findViewById<View>(R.id.list_msg) as ListView

        this.messages = mutableListOf()

        firestore.collection("Chat").document(currentUserId!!.email!!).collection("messages").get().addOnCompleteListener { task ->
            Log.d("LENGTH", task.result!!.size().toString())
            for(document in task.result!!) {
                Log.d("HAAAAAAA", document.toString())
                Log.d("CONTENT", document["content"].toString())
                Log.d("GELEZEN", document["gelezen"].toString())
                Log.d("MESSAGETIME", document["messageTime"].toString())
                Log.d("MESSAGEUSER", document["messageUser"].toString())

                var message = Message()
                message.content = document["content"].toString()

                message.gelezen = document["gelezen"].toString().equals("true")
                message.messageTime = document["messageTime"].toString().toLong()
                message.messageUser = document["messageUser"].toString()
                this.messages.add(this.messages.count(), message)
            }
            this.messages.sortBy { message -> message.messageTime }

            var listItems = arrayOfNulls<String>(this.messages.size)
            for(i in 0 until this.messages.size) {
                listItems[i] = this.messages[i].content + " - " + this.messages[i].messageUser
            }

            adapter =  ArrayAdapter(context!!, android.R.layout.simple_list_item_1, listItems.requireNoNulls())

            listView!!.adapter = adapter
        }
    }




}
