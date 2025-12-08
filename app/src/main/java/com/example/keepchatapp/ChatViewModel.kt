package com.example.keepchatapp

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel(){

    val messageList: SnapshotStateList<MessageModel> = mutableStateListOf()

    private val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = Constants.apiKey
    )

    @RequiresApi(35)
    fun sendMessage (question : String ) {
        viewModelScope.launch {

            if(question.isBlank()) return@launch

            messageList.add(MessageModel(question, "user"))
            messageList.add(MessageModel("Loading...", "model"))

            try {
                val history = messageList
                    .dropLast(2)  // Remove current question and "Loading..."
                    .map {
                        content(role = it.role) {
                            text(it.message)
                        }
                    }

                val chat = generativeModel.startChat(history=history)
                val response = chat.sendMessage(question)

                // Remove the "Loading..." message
                messageList.removeLast()

                // Add the model's response
                response.text?.let { modelResponse ->
                    messageList.add(MessageModel(modelResponse, "model"))
                }
            }
            catch (e : Exception){
                val lastMessage = messageList.lastOrNull()
                if (lastMessage != null && lastMessage.message == "Loading...") {
                    messageList.remove(lastMessage)
                }
                messageList.add(MessageModel("Error: ${e.message}", "model"))
                Log.e("ChatViewModel", "Error sending message", e)
            }

        }
    }
}
