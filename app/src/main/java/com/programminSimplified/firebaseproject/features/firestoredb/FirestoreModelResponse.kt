package com.programminSimplified.firebaseproject.features.firestoredb

data class FirestoreModelResponse(
    val item:FirestoreItem?,
    val key:String? = ""
){

    data class FirestoreItem(
        val title:String? = "",
        val description:String? = ""
    )

}
