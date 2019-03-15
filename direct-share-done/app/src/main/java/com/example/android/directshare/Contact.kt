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

import android.widget.TextView

/**
 * Provides the list of dummy contacts.
 * This sample implements this as constants, but real-life apps should use a database and such.
 */
data class Contact(val name: String) {

    val icon = R.mipmap.logo_avatar

    companion object {
        /**
         * Representative invalid contact ID.
         */
        val invalidId = -1

        /**
         * The contact ID.
         */
        val id = "contact_id"

        /**
         * The list of dummy contacts.
         */
        val contacts = arrayOf(
            Contact("Tereasa"),
            Contact("Chang"),
            Contact("Kory"),
            Contact("Clare"),
            Contact("Landon"),
            Contact("Kyle"),
            Contact("Deana"),
            Contact("Daria"),
            Contact("Melisa"),
            Contact("Sammie")
        )

        /**
         * Finds a [Contact] specified by a contact ID.
         *
         * @param id The contact ID. This needs to be a valid ID.
         * @return A [Contact]
         */
        fun byId(id: Int) = contacts[id]
    }
}

fun Contact.bind(textView: TextView) {
    with(textView) {
        text = name
        setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0)
    }
}
