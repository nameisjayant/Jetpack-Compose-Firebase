package com.programminSimplified.firebaseproject.firestoredb.repository

import com.programminSimplified.firebaseproject.firestoredb.FirestoreModelResponse
import com.programminSimplified.firebaseproject.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {

    fun insert(
        item:FirestoreModelResponse.FirestoreItem
    ) : Flow<ResultState<String>>

    fun getItems() : Flow<ResultState<List<FirestoreModelResponse>>>

    fun delete(key:String) : Flow<ResultState<String>>

    fun update(
        item:FirestoreModelResponse
    ) : Flow<ResultState<String>>

}