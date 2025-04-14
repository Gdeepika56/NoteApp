package com.example.jetpacknotesapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpacknotesapp.firebase_bd.Chat
import com.example.jetpacknotesapp.viewmodel.ChatViewModel

@Composable
fun ChatScreen() {
    val chatViewModel: ChatViewModel = viewModel()

    Column {
        ChatMessages(chatViewModel)
        SendMessage(chatViewModel)
    }
}

@Composable
fun ChatMessages(chatViewModel: ChatViewModel) {
    val chatList by chatViewModel.chats.observeAsState(emptyList())

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(chatList) { chat ->
            Row(
                horizontalArrangement = if (chat.userType == 1) Arrangement.Start else Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Box(modifier = Modifier.size(40.dp).background(if (chat.userType == 1) Color.Gray else Color.Blue)) {
                    Text(if (chat.userType == 1) "User" else "Bot", modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = chat.message,
                    modifier = Modifier
                        .background(if (chat.userType == 1) Color.LightGray else Color.Cyan)
                        .padding(8.dp)
                        .wrapContentHeight()
                        .fillMaxWidth(0.6f),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun SendMessage(viewModel: ChatViewModel) {
    var newMessage by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf(1) } // Toggle between user types

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = newMessage,
            onValueChange = { newMessage = it },
            label = { Text("Type a message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { userType = if (userType == 1) 2 else 1 }) {
                Text(if (userType == 1) "Switch to Bot" else "Switch to User")
            }
            Button(
                onClick = {
                    if (newMessage.isNotEmpty()) {
                        val chat = Chat(userType = userType, message = newMessage)
                        viewModel.sendMessage(chat)
                        newMessage = ""
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}
