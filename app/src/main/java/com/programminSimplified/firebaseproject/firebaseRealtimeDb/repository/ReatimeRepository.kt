package com.programminSimplified.firebaseproject.firebaseRealtimeDb.repository

import com.programminSimplified.firebaseproject.firebaseRealtimeDb.RealtimeModelResponse
import com.programminSimplified.firebaseproject.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface ReatimeRepository {

    fun insert(
        item:RealtimeModelResponse.RealtimeItems
    ) : Flow<ResultState<String>>

    fun getItems() : Flow<ResultState<List<RealtimeModelResponse>>>

    fun delete(
        key:String
    ) : Flow<ResultState<String>>

    fun update(
        res:RealtimeModelResponse
    ) : Flow<ResultState<String>>

}