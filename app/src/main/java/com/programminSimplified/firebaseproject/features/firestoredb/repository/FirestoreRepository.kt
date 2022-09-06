package com.programminSimplified.firebaseproject.features.firestoredb.repository

import com.programminSimplified.firebaseproject.features.firestoredb.FirestoreModelResponse
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