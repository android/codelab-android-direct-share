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
import android.widget.Button
import android.widget.EditText
import androidx.core.content.FileProvider
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.ClipData
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Provides the landing screen of this sample. There is nothing particularly interesting here. All
 * the codes related to the Direct Share feature are in [SharingShortcutsManager].
 */
class MainActivity : Activity() {

    // Tag to log to console
    private val tag = "DShare.MainActivity"

    // Domain authority for our app FileProvider
    private val fileProviderAuthority = "com.example.android.directshare.fileprovider"

    // Cache directory to store images
    // This is the same path specified in the @xml/file_paths and accessed from the AndroidManifest
    private val imageCacheDir = "images"

    // Name of the file to use for the thumbnail image
    private val imageFile = "image.png"

    private lateinit var bodyEditText: EditText
    private lateinit var sharingShortcutsManager: SharingShortcutsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // View setup
        bodyEditText = findViewById(R.id.body)
        findViewById<Button>(R.id.share).setOnClickListener { v ->
            when (v.id) {
                R.id.share -> share()
            }
        }

        sharingShortcutsManager = SharingShortcutsManager().also {
            it.pushDirectShareTargets(this)
        }
    }

    /**
     * Emits a sample share [Intent].
     */
    private fun share() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, bodyEditText.text.toString())

        // (Optional) If you want a preview title, set it with Intent.EXTRA_TITLE
//        // TODO STEP 9.1 - Add title to the shared content
//        sharingIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.send_intent_title))
//        // TODO END STEP 9.1

        // (Optional) if you want a preview thumbnail, create a content URI and add it
        // The system only supports content URIs
//        // TODO STEP 9.2 - Add thumbnail to the shared content
//        val thumbnail = getClipDataThumbnail()
//        thumbnail?.let {
//            sharingIntent.clipData = it
//            sharingIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//        }
//        // TODO END STEP 9.2

        startActivity(Intent.createChooser(sharingIntent, null))
    }

    /**
     * Get ClipData thumbnail object that needs to be passed in the Intent.
     * It stores the launcher icon in the cache and retrieves in a content URI.
     * The ClipData object is created with the URI we get from the FileProvider.
     *
     * For this to work, you need to configure a FileProvider in the project. We added it to the
     * AndroidManifest.xml file where we can configure it. We added the images path where we
     * save the image to the @xml/file_paths file which tells the FileProvider where we intend to
     * request content URIs.
     *
     * @return thumbnail ClipData object to set in the sharing Intent.
     */
    private fun getClipDataThumbnail(): ClipData? {
        return try {
            val contentUri = saveThumbnailImage()
            ClipData.newUri(contentResolver, null, contentUri)
        } catch (e: FileNotFoundException) {
            Log.e(tag, e.localizedMessage ?: "getClipDataThumbnail FileNotFoundException")
            null
        } catch (e: IOException) {
            Log.e(tag, e.localizedMessage ?: "getClipDataThumbnail IOException")
            null
        }
    }

    /**
     * Save our Launcher image to the cache and return it as a content:// URI.
     *
     * IMPORTANT: This could trigger StrictMode. Do not do this in your app.
     * For the simplicity of the code sample, this is running on the Main thread
     * but these tasks should be done in a background thread.
     *
     * @throws IOException if image couldn't be saved to the cache.
     * @return image content Uri
     */
    @Throws(IOException::class)
    private fun saveThumbnailImage(): Uri {
        val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val cachePath = File(applicationContext.cacheDir, imageCacheDir)
        cachePath.mkdirs()
        val stream = FileOutputStream("$cachePath/$imageFile")
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        val imagePath = File(cacheDir, imageCacheDir)
        val newFile = File(imagePath, imageFile)
        return FileProvider.getUriForFile(this, fileProviderAuthority, newFile)
    }
}
