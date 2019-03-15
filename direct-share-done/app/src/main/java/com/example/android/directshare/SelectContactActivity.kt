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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * The dialog for selecting a contact to share the text with. This dialog is shown when the user
 * taps on this sample's icon rather than any of the Direct Share contacts.
 */
class SelectContactActivity : Activity() {

    private val contactAdapter = object : RecyclerView.Adapter<ContactViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
            val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_contact, parent, false) as TextView
            return ContactViewHolder(textView)
        }

        override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
            val contact = Contact.contacts[position]
            contact.bind(holder.itemView as TextView)
            holder.itemView.setOnClickListener {
                val data = Intent()
                data.putExtra(Contact.id, position)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }

        override fun getItemCount() = Contact.contacts.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_contact)

        if (ACTION_SELECT_CONTACT != intent.action) {
            finish()
            return
        }

        // Set up the list of contacts
        findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = contactAdapter
            layoutManager = LinearLayoutManager(this@SelectContactActivity)
        }
    }

    companion object {
        /**
         * The action string for Intents.
         */
        const val ACTION_SELECT_CONTACT =
                "com.example.android.directshare.intent.action.SELECT_CONTACT"
    }
}

private class ContactViewHolder(textView: TextView) : RecyclerView.ViewHolder(textView)
