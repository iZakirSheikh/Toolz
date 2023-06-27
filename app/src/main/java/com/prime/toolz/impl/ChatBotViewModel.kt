package com.prime.toolz.impl

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prime.toolz.chatbot.ChatBot
import com.prime.toolz.core.gpt.ChatGPT
import com.prime.toolz.core.gpt.Message
import com.prime.toolz.core.gpt.send
import com.primex.preferences.Preferences
import com.primex.preferences.stringPreferenceKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID
import javax.inject.Inject

private const val TAG = "ChatBotViewModel"

/**
 * Builds [Message] from [JsonElement].
 *
 *  **Example**
 * ```json
 * {
 *    "message": {
 *        "id": "dec6d51c-e575-43ed-9a1e-aa02e33d762f",
 *        "author": {
 *            "role": "assistant",
 *            "name": null,
 *            "metadata": {}
 *        },
 *        "create_time": 1687681711.648454,
 *        "update_time": null,
 *        "content": {
 *            "content_type": "text",
 *            "parts": [""]
 *        },
 *        "status": "in_progress",
 *        "end_turn": null,
 *        "weight": 1.0,
 *        "metadata": {
 *            "message_type": "next",
 *            "model_slug": "text-davinci-002-render-sha"
 *        },
 *        "recipient": "all"
 *    },
 *    "conversation_id": "923cae1a-2ab0-45af-861a-45bac7af67fd",
 *    "error": null
 * }
 * ```
 *
 * @param json The [JsonElement] representation of message.
 * @return A [Message] object representing the parsed JSON string.
 */
private fun Message(json: JsonElement): Message {
    val messageObject = json.jsonObject["message"]?.jsonObject
    val contentParts = messageObject?.get("content")?.jsonObject?.get("parts")?.jsonArray
    val content = contentParts?.get(0)?.jsonPrimitive?.content ?: ""
    val id = messageObject?.get("id")?.jsonPrimitive?.content ?: UUID.randomUUID().toString()
    val role =
        messageObject?.get("author")?.jsonObject?.get("role")?.jsonPrimitive?.content ?: "user"
    val created =
        messageObject?.get("create_time")?.jsonPrimitive?.content?.toDoubleOrNull()?.toLong()
            ?: System.currentTimeMillis()
    return Message(content, id, role, created)
}

private val KEY_TOKEN = stringPreferenceKey(TAG + "_token");

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val preferences: Preferences,
    private val channel: SnackbarHostState,
) : ViewModel(), ChatBot {

    private val api = ChatGPT()

    @Deprecated("Find new solution.")
    private fun <T> Flow<T>.asComposeState(): State<T> {
        val state = mutableStateOf(runBlocking { first() })
        onEach { state.value = it }.launchIn(viewModelScope)
        return state
    }

    // replace this with settings.
    private val _token by preferences[KEY_TOKEN].asComposeState()
    private var _id: String? = null  //The conservation id; null if new.
    private val _conversation = mutableStateListOf<Message>()
    override val conversation: List<Message> get() = _conversation

    // Is the user still logged in.
    override val isLoggedIn: Boolean get() = !_token.isNullOrBlank()

    // Is the user still receiving the response.
    private var _processing by mutableStateOf(false)
    override val processing: Boolean get() = _processing

    // The current job.
    private var job: Job? = null

    private fun _clear() {
        job?.cancel()
        _id = null
        _conversation.clear()
        _processing = false
    }

    override fun clear() {
        viewModelScope.launch {
            val result = channel.showSnackbar(
                "Press CLEAR to start a new conversation.",
                "CLEAR",
                true,
                SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) _clear()
        }
    }

    override fun onLoggedIn(value: String) {
        viewModelScope.launch {
            with(channel) {
                // will allow to make calls easily
                // Case 1: When user wants to reset the access token
                if (value.isBlank()) {
                    // User wants to reset the access-token.
                    val action = showSnackbar(
                        "To reset your access token, please press the RESET button.",
                        "RESET",
                        true,
                        SnackbarDuration.Long
                    )
                    if (action == SnackbarResult.ActionPerformed) {
                        _clear() // clear conversation.
                        preferences[KEY_TOKEN] = ""
                    }
                    return@launch // return from here
                }
                // Case 2: When value is not blank
                val result = runCatching {
                    val element = Json.parseToJsonElement(value).jsonObject
                    element["accessToken"]?.jsonPrimitive?.content
                }
                val token = result.getOrNull()
                when (token == null) {
                    true -> showSnackbar(
                        message = "Error in obtaining access token. Please retry.",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )

                    else -> {
                        preferences[KEY_TOKEN] = token
                        showSnackbar("You're logged in! Make the most of it and enjoy!")
                    }
                }
            }
        }
    }

    override fun send(value: String) {
        // Here, we are sure that the job will only be called when the previous job was completed.
        // Currently, we are not sure what will happen if the previous job was not successful.
        // We launch a coroutine to handle the job and inform the user that processing has started.
        job = viewModelScope.launch {
            // Inform the user that processing has started.
            _processing = true
            // Obtain the parent ID or generate a new one.
            val parentID = _conversation.firstOrNull()?.id ?: UUID.randomUUID().toString()
            // Create a message to be asked.
            val asking = Message(content = value)
            // Append the message to the list at the start.
            _conversation.add(0, asking)
            // Call the API to send the message and handle the response.
            val result = runCatching {
                //TODO: Remove the global token from this API call.
                val response = api.send(_token!!, asking, parentID, conversationId = _id)
                val reader = BufferedReader(InputStreamReader(response.byteStream()))
                // Read each line of the response.
                var line: String? = null
                while (reader.readLine().also { line = it } != null) {
                    // Drop blank lines.
                    if (line.isNullOrBlank() || line == "event: ping") continue
                    // If the line is done, break out of the loop.
                    if (line == "data: [DONE]") {
                        _processing = false
                        break
                    }
                    // Drop the "data:" prefix from the line.
                    val msg = line?.drop(6) ?: continue
                    // If the JSON is blank or not a JSON_OBJECT, continue.
                    if (msg.isBlank() || msg.getOrNull(0) != '{') continue
                    // Parse the JSON into a JSON element.
                    val element = Json.parseToJsonElement(msg)
                    // Construct a message from the JSON element.
                    val message = Message(json = element)
                    // Skip this message if its role is "system".
                    if (message.role == "system") continue
                    // Handle this case only once.
                    if (_id == null)
                        _id = element.jsonObject["conversation_id"]?.jsonPrimitive?.content
                    // If the conversation is not empty and the first message has the same ID as the current message, replace it.
                    // Otherwise, add the new message to the beginning of the conversation list.
                    if (conversation.isNotEmpty() && conversation[0].id == message.id)
                        _conversation[0] = message
                    else _conversation.add(0, message)
                    // In order to feel like real one.
                    delay(5)
                }
            }
            // If the API call failed, handle the error.
            if (result.isFailure) {
                // Reset the conversation to null and ask the user to submit the issue in GitHub as
                // a bug, allowing them to copy the error.
                // Alternatively, simply post the error in CrashAnalytics.
                val res = channel.showSnackbar(
                    "There was a problem. Please try resetting the chat or check back later.",
                    "RESET",
                    true,
                    SnackbarDuration.Long
                )
                if (res == SnackbarResult.ActionPerformed) _clear()
            }
        }
    }
}