package com.trimad.ichat.firestoragecontroller

import android.net.Uri
import com.google.firebase.storage.StorageReference
import com.trimad.ichat.listeners.OnFileUploadListener

object FireStorageUploader {
    fun uploadFile(
        storageReference: StorageReference,
        uri: Uri,
        onFileUploadListener: OnFileUploadListener
    ) {
        storageReference.child(uri.lastPathSegment!!).putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    onFileUploadListener.onFileUploaded(uri.toString())
                }
            }
            .addOnProgressListener { snapshot -> onFileUploadListener.onProgress(snapshot) }
            .addOnFailureListener { e -> onFileUploadListener.onFailure(e.message) }
    }
}