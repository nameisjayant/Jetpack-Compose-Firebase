package com.programminSimplified.firebaseproject.features.firebaseRealtimeDb.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.programminSimplified.firebaseproject.features.firebaseRealtimeDb.RealtimeModelResponse
import com.programminSimplified.firebaseproject.utils.ResultState
import com.programminSimplified.firebaseproject.utils.showMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun RealtimeScreen(
    isInsert: MutableState<Boolean>,
    viewModel: RealtimeViewModel = hiltViewModel()
) {

    val title = remember { mutableStateOf("") }
    val des = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isDialog = remember { mutableStateOf(false) }
    val res = viewModel.res.value
    val isUpdate = remember{ mutableStateOf(false)}


    if (isInsert.value) {
        AlertDialog(onDismissRequest = { isInsert.value = false },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(value = title.value, onValueChange = {
                        title.value = it
                    },
                        placeholder = { Text(text = "Title") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(value = des.value, onValueChange = {
                        des.value = it
                    },
                        placeholder = { Text(text = "description") }
                    )
                }
            },
            buttons = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center) {
                    Button(onClick = {
                        scope.launch(Dispatchers.Main) {
                            viewModel.insert(
                                RealtimeModelResponse.RealtimeItems(
                                    title.value,
                                    des.value
                                )
                            ).collect {
                                when (it) {
                                    is ResultState.Success -> {
                                        context.showMsg(
                                            msg = it.data
                                        )
                                        isDialog.value = false
                                        isInsert.value = false
                                    }
                                    is ResultState.Failure -> {
                                        context.showMsg(
                                            msg = it.msg.toString()
                                        )
                                        isDialog.value = false
                                    }
                                    ResultState.Loading -> {
                                        isDialog.value = true
                                    }
                                }
                            }
                        }
                    }) {
                        Text(text = "Save")
                    }
                }
            }
        )
    }

    if(isUpdate.value){
        Update(isUpdate = isUpdate,
            itemState = viewModel.updateRes.value,
            viewModel = viewModel )
    }

    if (res.item.isNotEmpty()) {

        LazyColumn {
            items(
                res.item,
                key = {
                    it.key!!
                }
            ) { res ->
                EachRow(itemState = res.item!!,
                    onUpdate = {
                        isUpdate.value = true
                        viewModel.setData(res)
                    }
                ){
                    scope.launch(Dispatchers.Main){
                        viewModel.delete(res.key!!).collect{
                            when (it) {
                                is ResultState.Success -> {
                                    context.showMsg(
                                        msg = it.data
                                    )
                                    isDialog.value = false
                                }
                                is ResultState.Failure -> {
                                    context.showMsg(
                                        msg = it.msg.toString()
                                    )
                                    isDialog.value = false
                                }
                                ResultState.Loading -> {
                                    isDialog.value = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (res.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
            CircularProgressIndicator()
        }
    }

    if (res.error.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
            Text(res.error)
        }
    }

}

@Composable
fun EachRow(
    itemState: RealtimeModelResponse.RealtimeItems,
    onUpdate: () -> Unit = {},
    onDelete: () -> Unit = {}
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 1.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onUpdate()
            }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = itemState.title!!,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                    IconButton(
                        onClick = {
                            onDelete()
                        },
                        modifier = Modifier.align(
                            CenterVertically
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "", tint = Color.Red)
                    }
                }
                Text(
                    text = itemState.description!!,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                )
            }
        }
    }

}

@Composable
fun Update(
    isUpdate:MutableState<Boolean>,
    itemState: RealtimeModelResponse,
    viewModel: RealtimeViewModel
) {
    val title = remember { mutableStateOf(itemState.item?.title) }
    val des = remember { mutableStateOf(itemState.item?.description) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if(isUpdate.value){

        AlertDialog(onDismissRequest = { isUpdate.value = false },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(value = title.value!!, onValueChange = {
                        title.value = it
                    },
                        placeholder = { Text(text = "Title") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(value = des.value!!, onValueChange = {
                        des.value = it
                    },
                        placeholder = { Text(text = "description") }
                    )
                }
            },
            buttons = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center) {
                    Button(onClick = {
                        scope.launch(Dispatchers.Main) {
                            viewModel.update(
                                RealtimeModelResponse(
                                    item = RealtimeModelResponse.RealtimeItems(
                                        title.value,
                                        des.value
                                    ),
                                    key = itemState.key
                                )
                            ).collect {
                                when (it) {
                                    is ResultState.Success -> {
                                        context.showMsg(
                                            msg = it.data
                                        )
                                        isUpdate.value = false

                                    }
                                    is ResultState.Failure -> {
                                        context.showMsg(
                                            msg = it.msg.toString()
                                        )
                                    }
                                    ResultState.Loading -> {
                                    }
                                }
                            }
                        }
                    }) {
                        Text(text = "Save")
                    }
                }
            }
        )

    }

}