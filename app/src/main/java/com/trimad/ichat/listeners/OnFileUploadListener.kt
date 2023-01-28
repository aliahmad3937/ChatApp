package com.trimad.ichat.listeners

import com.google.firebase.storage.UploadTask

interface OnFileUploadListener {
    fun onFileUploaded(url: String?)
    fun onProgress(snapshot: UploadTask.TaskSnapshot?)
    fun onFailure(e: String?)
}