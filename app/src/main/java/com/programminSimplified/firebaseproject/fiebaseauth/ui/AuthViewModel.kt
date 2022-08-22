package com.programminSimplified.firebaseproject.fiebaseauth.ui

import androidx.lifecycle.ViewModel
import com.programminSimplified.firebaseproject.fiebaseauth.AuthUser
import com.programminSimplified.firebaseproject.fiebaseauth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo:AuthRepository
) : ViewModel() {

    fun createUser(authUser: AuthUser) = repo.createUser(authUser)

    fun loginUser(authUser: AuthUser) = repo.loginUser(authUser)

}