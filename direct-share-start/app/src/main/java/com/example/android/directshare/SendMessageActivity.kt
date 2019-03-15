/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.directshare

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

/**
 * Provides the UI for sharing a text with a [Contact].
 */
class SendMessageActivity : Activity() {

    /**
     * The request code for [SelectContactActivity]. This is used when the user doesn't
     * select any of Direct Share icons.
     */
    private val requestSelectContact = 1

    /**
     * The text to share.
     */
    private var textToShare: String? = null

    /**
     * The ID of the contact to share the text with.
     */
    private var contactId: Int = 0

    // View references.
    private lateinit var textContactName: TextView
    private lateinit var textMessageBody: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)
        setTitle(R.string.sending_message)

        // View references.
        textContactName = findViewById(R.id.contact_name)
        textMessageBody = findViewById(R.id.message_body)

        // Handle the share Intent.
        val handled = handleIntent(intent)
        if (!handled) {
            finish()
            return
        }

        // Bind event handlers.
        findViewById<Button>(R.id.send).setOnClickListener { v ->
            when (v.id) {
                R.id.send -> send()
            }
        }

        // Set up the UI.
        prepareUi()

        // The contact ID will not be passed on when the user clicks on the app icon rather than any
        // of the Direct Share icons. In this case, we show another dialog for selecting a contact.
        if (contactId == Contact.invalidId) {
            selectContact()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            requestSelectContact -> {
                if (resultCode == Activity.RESULT_OK) {
                    contactId = data.getIntExtra(Contact.id, Contact.invalidId)
                }
                // Give up sharing the send_message if the user didn't choose a contact.
                if (contactId == Contact.invalidId) {
                    finish()
                    return
                }
                prepareUi()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * Handles the passed [Intent]. This method can only handle intents for sharing a plain
     * text. [textToShare] and [contactId] are modified accordingly.
     *
     * @param intent The [Intent].
     * @return true if the [intent] is handled properly.
     */
    private fun handleIntent(intent: Intent): Boolean {
        if (Intent.ACTION_SEND == intent.action && "text/plain" == intent.type) {
            textToShare = intent.getStringExtra(Intent.EXTRA_TEXT)
            // The intent comes from Direct share
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
                    intent.hasExtra(Intent.EXTRA_SHORTCUT_ID)) {
                val shortcutId = intent.getStringExtra(Intent.EXTRA_SHORTCUT_ID)
                contactId = Integer.valueOf(shortcutId!!)
            } else {
                // The text was shared and the user chose our app
                contactId = Contact.invalidId
            }
            return true
        }
        return false
    }

    /**
     * Sets up the UI.
     */
    private fun prepareUi() {
        if (contactId != Contact.invalidId) {
            val contact = Contact.byId(contactId)
            contact.bind(textContactName)
        }
        textMessageBody.text = textToShare
    }

    /**
     * Delegates selection of a {@Contact} to [SelectContactActivity].
     */
    private fun selectContact() {
        val intent = Intent(this, SelectContactActivity::class.java)
        intent.action = SelectContactActivity.ACTION_SELECT_CONTACT
        startActivityForResult(intent, requestSelectContact)
    }

    /**
     * Pretends to send the text to the contact. This only shows a dummy message.
     */
    private fun send() {
        Toast.makeText(this,
                getString(R.string.message_sent, textToShare, Contact.byId(contactId).name),
                Toast.LENGTH_SHORT).show()
        finish()
    }
}
