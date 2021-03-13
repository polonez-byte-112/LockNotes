package com.safenotes.models

data class Note(
        var note_user_id: String,
        var note_name: String,
        val note_text: String,
        val note_date: String
)
