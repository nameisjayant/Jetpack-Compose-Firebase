package com.programminSimplified.firebaseproject.firestoredb

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.programminSimplified.firebaseproject.common.CommonDialog
import com.programminSimplified.firebaseproject.firebaseRealtimeDb.RealtimeModelResponse
import com.programminSimplified.firebaseproject.firestoredb.ui.FirestoreViewModel
import com.programminSimplified.firebaseproject.utils.ResultState
import com.programminSimplified.firebaseproject.utils.showMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun FirestoreScreen(
    isInput: MutableState<Boolean>,
    viewModel: FirestoreViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var des by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isDialog = remember { mutableStateOf(false) }
    val res = viewModel.res.value
    val isUpdate = remember { mutableStateOf(false) }

    if (isDialog.value)
        CommonDialog()

    if (isInput.value) {

        AlertDialog(onDismissRequest = { isInput.value = false },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    TextField(value = title, onValueChange = {
                        title = it
                    },
                        placeholder = {
                            Text(text = "Enter title")
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(value = des, onValueChange = {
                        des = it
                    },
                        placeholder = {
                            Text(text = "Enter description")
                        }
                    )
                }
            },
            buttons = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = {
                        scope.launch(Dispatchers.Main) {
                            viewModel.insert(
                                FirestoreModelResponse.FirestoreItem(
                                    title,
                                    des
                                )
                            ).collect {
                                when (it) {
                                    is ResultState.Success -> {
                                        isInput.value = false
                                        isDialog.value = false
                                        context.showMsg(it.data)
                                        viewModel.getItems()
                                    }
                                    is ResultState.Failure -> {
                                        isDialog.value = false
                                        context.showMsg(it.msg.toString())
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


    if (isUpdate.value)
        UpdateData(
            item = viewModel.updateData.value,
            viewModel = viewModel,
            isUpdate = isUpdate,
            isDialog = isDialog
        )


    if (res.data.isNotEmpty())
        LazyColumn {
            items(
                res.data,
                key = {
                    it.key!!
                }
            ) { items ->
                EachRow1(itemState = items,
                    onUpdate = {
                        isUpdate.value = true
                        viewModel.setData(items)
                    }
                ){
                    scope.launch(Dispatchers.Main){
                        viewModel.delete(items.key!!)
                            .collect {
                                when (it) {
                                    is ResultState.Success -> {
                                        isDialog.value = false
                                        context.showMsg(it.data)
                                        viewModel.getItems()
                                    }
                                    is ResultState.Failure -> {
                                        isDialog.value = false
                                        context.showMsg(it.msg.toString())
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

    if (res.isLoading)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

    if (res.error.isNotEmpty())
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(res.error)
        }
}


@Composable
fun EachRow1(
    itemState: FirestoreModelResponse,
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
                        text = itemState.item?.title!!,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.align(
                            Alignment.CenterVertically
                        )
                    )
                    IconButton(
                        onClick = {
                            onDelete()
                        },

                        ) {
                        Icon(Icons.Default.Delete, contentDescription = "", tint = Color.Red)
                    }
                }
                Text(
                    text = itemState.item?.description!!,
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
fun UpdateData(
    item: FirestoreModelResponse,
    viewModel: FirestoreViewModel,
    isUpdate: MutableState<Boolean>,
    isDialog: MutableState<Boolean>
) {

    var title by remember { mutableStateOf(item.item?.title) }
    var des by remember { mutableStateOf(item.item?.description) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    AlertDialog(onDismissRequest = { isUpdate.value = false },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                TextField(value = title!!, onValueChange = {
                    title = it
                },
                    placeholder = {
                        Text(text = "Enter title")
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(value = des!!, onValueChange = {
                    des = it
                },
                    placeholder = {
                        Text(text = "Enter description")
                    }
                )
            }
        },
        buttons = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = {
                    scope.launch(Dispatchers.Main) {
                        viewModel.update(
                            FirestoreModelResponse(
                                item = FirestoreModelResponse.FirestoreItem(
                                    title,
                                    des
                                ),
                                key = item.key
                            )
                        ).collect {
                            when (it) {
                                is ResultState.Success -> {
                                    isUpdate.value = false
                                    isDialog.value = false
                                    context.showMsg(it.data)
                                    viewModel.getItems()
                                }
                                is ResultState.Failure -> {
                                    isDialog.value = false
                                    context.showMsg(it.msg.toString())
                                }
                                ResultState.Loading -> {
                                    isDialog.value = true
                                }
                            }
                        }
                    }
                }) {
                    Text(text = "Update")
                }
            }
        }
    )


}