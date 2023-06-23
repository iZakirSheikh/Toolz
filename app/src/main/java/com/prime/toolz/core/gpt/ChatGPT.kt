package com.prime.toolz.core.gpt

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.UUID
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

private const val TAG = "ChatGPT"

/**
 * Represents a message object that can be passed to and from the chatGPT.
 *
 * @property content The content of the message.
 * @property id The unique identifier of the message. It is generated using a random UUID if not provided.
 * @property role The role of the sender of the message. Possible values are "system", "user", "assistant", or "function".
 * @property created The timestamp indicating when the message was created. It is set to the current system time if not provided.
 */
data class Message(
    val content: String,
    val id: String = UUID.randomUUID().toString(),
    val role: String = "user", // any of system, user, assistant, or function.
    val created: Long = System.currentTimeMillis(),
)


/**
 * Converts the message into a JSON array format that can be used with a [ChatGPT] request.
 *
 * The generated JSON array format follows the structure expected by ChatGPT and includes the message details such as ID, author role, content, and metadata.
 *
 * Example JSON array format:
 * ```
 * {
 *   "action": "next",
 *   "messages": [
 *     {
 *       "id": "aaa2b5c1-9a05-4959-9e78-6580c7be6047",
 *       "author": {
 *         "role": "user"
 *       },
 *       "content": {
 *         "content_type": "text",
 *         "parts": [
 *           "hi\n"
 *         ]
 *       },
 *       "metadata": {}
 *     }
 *   ],
 *   "parent_message_id": "aaa1e9d7-b67d-42b6-a732-562e8a844c48",
 *   "model": "text-davinci-002-render-sha",
 *   "timezone_offset_min": -330,
 *   "history_and_training_disabled": false,
 *   "arkose_token": null
 * }
 * ```
 *
 * @return The message converted to a JSON array.
 */
private val Message.toJsonArray
    get() = buildJsonArray {
        add(
            buildJsonObject {
                put("id", id)
                putJsonObject("author") {
                    put("role", role)
                }
                putJsonObject("content") {
                    put("content_type", "text")
                    putJsonArray("parts") {
                        add(content)
                    }
                }
                putJsonObject("metadata") {}
            }
        )
    }

private const val USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
private const val CHAT_GPT_MODEL = "text-davinci-002-render-sha"

interface ChatGPT {

    /**
     * Sends a request [Message] as [RequestBody] and receives [ResponseBody] as a response.
     * The response body is a stream and can be parsed as a Buffered Stream.
     *
     * This function is used to send a request to the backend API's conversation endpoint. It requires the following parameters:
     * [token]: A string containing the authorization token for the request, passed as a header.
     * [body]: The request body containing the message to be sent, passed as a JSON object.
     *
     * The function uses the HTTP POST method to send the request to the specified endpoint.
     *
     *@param token The authorization token for the request.  Append with 'Bearer'
     *@param body The request body containing the message to be sent.
     *@return The response body as a stream, which can be parsed as a Buffered Stream.
     *
     * @see <a href="https://github.com/terminalcommandnewsletter/everything-chatgpt/blob/520e6cd6d78d1d08ab4f0c863316429c76a6a540/client-side-js/%5BNot%20used%20anymore%5D%20264-13e92c51b0315184.js">source</a>
     */
    @Headers(
        "accept: text/event-stream",
        "accept-encoding: gzip, deflate, br",
        "accept-language: en-GB,en-US;q=0.9,en;q=0.8",
        "content-type: application/json",
        "referer: https://chat.openai.com/chat",
        "User-Agent: $USER_AGENT"
    )
    @Deprecated("Use the other send.")
    @POST("backend-api/conversation")
    suspend fun send(@Header("Authorization") token: String, @Body body: RequestBody): ResponseBody

    companion object {
        operator fun invoke(): ChatGPT {
            val contentType = MediaType.get("application/json")
            return Retrofit.Builder()
                .baseUrl("https://chat.openai.com/")
                //.addConverterFactory(Json.asConverterFactory(contentType))
                .client(
                    OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build()
                )
                .build()
                .create()
        }
    }
}

/**
 * Sends a request to the backend API's conversation endpoint and receives the response as a stream.
 *
 * This function is a suspending function, which means it can be called from a coroutine.
 *
 * @param token The authorization token for the request.
 * @param message The message to be sent.
 * @param parentID The ID of the parent message. It is a non-null and non-empty string. Defaults to a randomly generated UUID if not provided.
 * @param conversationId The ID of the conversation. If provided, the message will be added to an existing conversation. If not provided, a new conversation will be created.
 * @param model The name of the model to be used for generating the response. Defaults to [CHAT_GPT_MODEL].
 * @return The response body as a stream, which can be parsed as a Buffered Stream.
 *
@see source */
suspend fun ChatGPT.send(
    token: String,
    message: Message,
    parentID: String = UUID.randomUUID().toString(), // non-null + non_empty
    conversationId: String? = null, // creates a new conversation.
    model: String = CHAT_GPT_MODEL
): ResponseBody {
    // construct the request.
    val request = buildJsonObject {
        put("action", "next")
        put("messages", message.toJsonArray)
        if (conversationId != null)
            put("conversation_id", conversationId)
        put("parent_message_id", parentID)
        put("model", model)
        //put("timezone_offset_min", -330)
        //put("history_and_training_disabled", false)
        //put("arkose_token", JsonNull)
    }
    val body =
        RequestBody.create(MediaType.get("application/json"), request.toString().trimIndent())
    return send("Bearer $token", body)
}