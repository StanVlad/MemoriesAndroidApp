package com.example.memoriesv3

import android.net.Uri;

class Memory {

    private lateinit var name: String
    private lateinit var uri: Uri

    fun get_name(): String {
        return name
    }

    fun set_name(name: String) {
        this.name = name;
    }

    fun get_uri(): Uri {
        return uri
    }

    fun set_uri(uri: Uri) {
        this.uri = uri;
    }
}