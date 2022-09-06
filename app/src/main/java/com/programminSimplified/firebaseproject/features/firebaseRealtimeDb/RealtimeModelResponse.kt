package com.programminSimplified.firebaseproject.features.firebaseRealtimeDb

data class RealtimeModelResponse(
    val item:RealtimeItems?,
    val key:String? = ""
){

    data class RealtimeItems(
        val title:String? = "",
        val description:String? = ""
    )

}
