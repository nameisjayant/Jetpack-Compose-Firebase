package com.programminSimplified.firebaseproject.features.firebaseRealtimeDb.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programminSimplified.firebaseproject.features.firebaseRealtimeDb.RealtimeModelResponse
import com.programminSimplified.firebaseproject.features.firebaseRealtimeDb.repository.ReatimeRepository
import com.programminSimplified.firebaseproject.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RealtimeViewModel @Inject constructor(
    private val repo:ReatimeRepository
) : ViewModel(){

    private val _res:MutableState<ItemState> = mutableStateOf(ItemState())
    val res:State<ItemState> = _res

    fun insert(items: RealtimeModelResponse.RealtimeItems) = repo.insert(items)

    private val _updateRes:MutableState<RealtimeModelResponse> = mutableStateOf(
        RealtimeModelResponse(
            item = RealtimeModelResponse.RealtimeItems(),
        )
    )
    val updateRes:State<RealtimeModelResponse> = _updateRes

    fun setData(data:RealtimeModelResponse){
        _updateRes.value = data
    }

    init {

        viewModelScope.launch {
            repo.getItems().collect{
                when(it){
                    is ResultState.Success -> {
                        _res.value = ItemState(
                            item = it.data
                        )
                    }
                    is ResultState.Failure->{
                        _res.value = ItemState(
                            error = it.msg.toString()
                        )
                    }
                    ResultState.Loading->{
                        _res.value = ItemState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    fun delete(key:String) = repo.delete(key)
    fun update(item:RealtimeModelResponse) = repo.update(item)


}

data class ItemState(
    val item:List<RealtimeModelResponse> = emptyList(),
    val error:String  = "",
    val isLoading:Boolean = false
)
