package com.example.jetpacknotesapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jetpacknotesapp.firebase_bd.Chat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class ChatViewModel : ViewModel() {

    private val _chats = MutableLiveData<List<Chat>>()
    val chats: LiveData<List<Chat>> = _chats
    private val firebaseRealtime = FirebaseDatabase.getInstance()
    private val chatDatabase = firebaseRealtime.getReference("chats")

    init {
        fetchChats()
    }

    private fun fetchChats() {
        chatDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatMessages = snapshot.children.mapNotNull { it.getValue<Chat>() }
                _chats.value = chatMessages
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendMessage(chat: Chat) {
        val id = chatDatabase.push().key
        id?.let {
            chatDatabase.child(it).setValue(chat)
        }
        fetchChats()
    }
}