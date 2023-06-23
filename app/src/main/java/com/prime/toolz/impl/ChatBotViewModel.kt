package com.prime.toolz.impl

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import com.prime.toolz.chatbot.ChatBot
import com.prime.toolz.core.gpt.Message
import com.primex.preferences.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "ChatBotViewModel"

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val preferences: Preferences,
    private val channel: SnackbarHostState,
) : ViewModel(), ChatBot {

    override val conversation: List<Message>
        get() = TODO("Not yet implemented")

    override fun send(value: String) {
        TODO("Not yet implemented")
    }

    override val isInProgress: Boolean
        get() = TODO("Not yet implemented")
    override val hasAccess: Boolean
        get() = TODO("Not yet implemented")

    override fun onLoggedIn(value: String) {
        TODO("Not yet implemented")
    }
}