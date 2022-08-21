package com.programminSimplified.firebaseproject.firestoredb.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programminSimplified.firebaseproject.firestoredb.FirestoreModelResponse
import com.programminSimplified.firebaseproject.firestoredb.repository.FirestoreRepository
import com.programminSimplified.firebaseproject.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirestoreViewModel @Inject constructor(
    private val repo:FirestoreRepository
) : ViewModel(){

    private val _res:MutableState<FirestoreState> = mutableStateOf(FirestoreState())
    val res:State<FirestoreState> = _res

    fun insert(item:FirestoreModelResponse.FirestoreItem) = repo.insert(item)
    private val _updateData:MutableState<FirestoreModelResponse> = mutableStateOf(
        FirestoreModelResponse(
        item = FirestoreModelResponse.FirestoreItem()
    )
    )
    val updateData:State<FirestoreModelResponse> = _updateData

    fun setData(data:FirestoreModelResponse){
        _updateData.value = data
    }

    init {
        getItems()
    }

    fun getItems() = viewModelScope.launch {
        repo.getItems().collect{
            when(it){
                is ResultState.Success->{
                    _res.value = FirestoreState(
                        data = it.data
                    )
                }
                is ResultState.Failure->{
                    _res.value = FirestoreState(
                        error = it.msg.toString()
                    )
                }
                ResultState.Loading->{
                    _res.value = FirestoreState(
                        isLoading = true
                    )
                }
            }
        }
    }

    fun delete(key:String) = repo.delete(key)
    fun update(item:FirestoreModelResponse) = repo.update(item)

}

data class FirestoreState(
    val data:List<FirestoreModelResponse> = emptyList(),
    val error:String = "",
    val isLoading:Boolean = false
)