package com.programminSimplified.firebaseproject.fiebaseauth.repository

import com.programminSimplified.firebaseproject.fiebaseauth.AuthUser
import com.programminSimplified.firebaseproject.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun createUser(
        auth:AuthUser
    ) : Flow<ResultState<String>>

    fun loginUser(
        auth:AuthUser
    ) : Flow<ResultState<String>>

}