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
    val isInProgress: Boolean

    /**
     * Indicates whether this app has access to the back-end server.
     *
     * @return true if this app has access, false otherwise.
     */
    val hasAccess: Boolean

    /**
     * Called after receiving a cookie from the browser. Pass an empty string to re-ask for login.
     *
     * @param value The value of the received cookie.
     */
    fun onLoggedIn(value: String)
}