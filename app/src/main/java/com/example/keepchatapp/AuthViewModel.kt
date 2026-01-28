package com.example.keepchatapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth


class AuthViewModel: ViewModel(){

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    fun isUserLoggedIn(): Boolean{
        return auth.currentUser != null
    }

    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState

    init {
        checkAuthState()
    }

    fun checkAuthState() {

        if(auth.currentUser != null){
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email and Password cannot be empty")
            return
        }

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{ work ->
                if(work.isSuccessful){
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(work.exception?.message ?: "Something went Wrong")
                }
            }

    }

    fun createAccount(email: String, password: String) {

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Enter valid Email and Password")
            return
        }

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{ work ->
                if(work.isSuccessful){
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(work.exception?.message ?: "Something went Wrong")
                }
            }

    }


    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }



    
}


sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()

    data class Error(
        val message: String
    ) : AuthState()
}