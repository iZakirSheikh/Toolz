package com.prime.toolz.chatbot

import com.prime.toolz.core.gpt.Message

/**
 * An interface representing state of a chat bot.
 */
interface ChatBot {
    companion object {
        const val route = "route_chat_bot"

        /**
         * Provides the direction for the [ChatBot] route.
         *
         * @return the route for [ChatBot]
         */
        fun direction() = route
    }

    /**
     * A conversation is a list of messages, where each subsequent message is a child of the previous one.
     */
    val conversation: List<Message>

    /**
     * Sends a message. The message will be appended to the [conversation] list.
     *
     * @param message The message to be sent.
     */
    fun send(value: String)

    /**
     * The state of the chat bot.
     */
    val processing: Boolean

    /**
     * Indicates whether this app has access to the back-end server.
     *
     * @return true if this app has access, false otherwise.
     */
    val isLoggedIn: Boolean

    /**
     * Sets or saves persistently the access token provided by the user.
     *
     * Example JSON input:
     * ```
     * {
     *     "user": {
     *         "id": "user-sdsdsgfghhfgjgj",
     *         "name": "Zakir Sheikh",
     *         "email": "feedbacktoprime@gmail.com",
     *         "image": "https://lh3.googleusercontent.com/s96-c",
     *         "picture": "https://lh3.googleusercontent.com/a/fy60ks=s96-c",
     *         "idp": "google-oauth2",
     *         "iat": 1686933542,
     *         "mfa": false,
     *         "groups": [],
     *         "intercom_hash": "d66746f97303663eb50b9a5cd43286d135c3f36daf19949"
     *     },
     *     "expires": "2023-07-27T04:19:42.475Z",
     *     "accessToken": "access token",
     *     "authProvider": "auth0"
     * }
     * ```
     * @param value The JSON string containing the access token. If an empty string is passed, the old saved value will be removed.
     */
    fun onLoggedIn(value: String)

    /** Clears the conversation and starts a new one. */
    fun clear()
}