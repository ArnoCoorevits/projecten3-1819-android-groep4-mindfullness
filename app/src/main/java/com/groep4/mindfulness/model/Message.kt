package com.groep4.mindfulness.model;

/**
 * Simpele Class voor chat
 */

class Message {
    var content: String? = ""
    var messageUser: String? = ""
    var messageTime: Long = 0
    var gelezen: Boolean = false

    /**
     * Chat list heeft no-args constructor nodig.
     */
    constructor()

    constructor(content: String, messageUser: String){
        this.content = content
        this.messageUser = messageUser
    }

    constructor(content: String, gelezen: Boolean, messageTime: Long, messageUser: String){
        this.content = content
        this.gelezen = gelezen
        this.messageUser = messageUser
        this.messageTime = messageTime
    }
}