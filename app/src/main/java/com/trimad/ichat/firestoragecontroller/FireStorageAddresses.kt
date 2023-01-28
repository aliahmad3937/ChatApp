package com.trimad.ichat.firestoragecontroller

import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage

object FireStorageAddresses {
    val groupStorage: StorageReference
        get() = FirebaseStorage.getInstance().getReference("GroupImages")
}