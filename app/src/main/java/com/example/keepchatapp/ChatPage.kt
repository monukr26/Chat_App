package com.example.keepchatapp

import androidx.annotation.RequiresApi
import androidx.benchmark.perfetto.Row
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keepchatapp.ui.theme.Pink80

@RequiresApi(35)
@Composable
fun ChatPage ( modifier: Modifier = Modifier, viewModel: ChatViewModel){
    Column (modifier = modifier.fillMaxSize()){
        AppHeader()
        MessageList(modifier = Modifier.weight(1f),
            messageList = viewModel.messageList
        )

        MessageInput(onMessageSend = {
            viewModel.sendMessage(it)

        })

    }

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
            .padding(22.dp),
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


@Composable
fun AppHeader () {
    Box (modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 32.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "KeepChat",
            color = Color.Black,
            fontFamily = FontFamily.Cursive,
            fontSize = 36.sp

        )


    }

}




