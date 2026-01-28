package com.example.keepchatapp

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(35)
@Composable
fun ChatPage ( modifier: Modifier = Modifier,navController: NavController, viewModel: ChatViewModel, authModel: AuthViewModel){

    val authState = authModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> {
                navController.navigate("login")
            }
            else -> Unit
        }
    }
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(8.dp),
                title = {
                    Text(
                        text = "KeepChat",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Cursive
                    )
                },

                navigationIcon = {
                    IconButton(onClick = {},
                        modifier = Modifier
                            .background(Color(0x229DCFF1), CircleShape)) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(24.dp)
                        )

                    }
                }
            )
        },
        bottomBar = {
            MessageInput(onMessageSend = {
                viewModel.sendMessage(it)
            })
        }
    ) { padding ->


    Column (modifier = modifier.fillMaxSize().padding(padding)){
        MessageList(modifier = Modifier.weight(1f),
            messageList = viewModel.messageList
        )
    }}

}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>){

    val listState = rememberLazyListState()

    if(messageList.isEmpty()){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
            )
        {
            Text(text = "Ask me Anything" , fontSize = 18.sp)
        }
    }
    else{
        LaunchedEffect(messageList.size) {
            if(messageList.isNotEmpty()) {
                listState.animateScrollToItem(messageList.lastIndex)
            }
        }
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            state = listState,
            reverseLayout = false,
            verticalArrangement = Arrangement.Top,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messageList) { msg ->
                MessageRow(messageModel = msg)
            }
        }

    }

}

@Composable
fun MessageRow ( messageModel: MessageModel ) {
    val isModel = messageModel.role=="model"
    val bubbleColor = if (isModel) Color(0xFFF8F8F8) else Color(0xFFE3F2FD)


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isModel) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (isModel) {
                        // Model message: full width
                        Modifier.fillMaxWidth(1f)
                    } else {
                        // User message: limited width
                        Modifier.widthIn(max = 280.dp)
                    }
                )
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bubbleColor)
                .padding(12.dp)

        ) {
            SelectionContainer {
                Text(
                    text = messageModel.message,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }

    }
}

@Composable
fun MessageInput (onMessageSend : (String) -> Unit) {

    var message by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 12.dp, bottom = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Type your message..") },
            maxLines = 4,
            singleLine = false
        )
        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
              if(message.isNotBlank()){
                onMessageSend(message.trim())
                message = ""
              }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send"
            )

        }

    }
}





