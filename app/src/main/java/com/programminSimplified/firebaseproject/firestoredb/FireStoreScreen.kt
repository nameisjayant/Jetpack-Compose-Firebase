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
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isDialog by remember { mutableStateOf(false) }
    val isUpdate = remember { mutableStateOf(false) }
    val res = viewModel.res.value
    val isRefresh = remember { mutableStateOf(false) }

    if (isDialog)
        CommonDialog()

    if (isInput.value) {

        AlertDialog(onDismissRequest = { isInput.value = false },
            title = { Text(text = "Add your List", modifier = Modifier.padding(vertical = 10.dp)) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(value = title, onValueChange = {
                        title = it
                    },
                        placeholder = { Text(text = "Enter title") }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    TextField(value = description, onValueChange = {
                        description = it
                    },
                        placeholder = { Text(text = "Enter description") }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        scope.launch(Dispatchers.Main) {
                            viewModel.insert(
                                FirestoreModelResponse.FirestoreItem(
                                    title,
                                    description
                                )
                            ).collect {
                                when (it) {
                                    is ResultState.Success -> {
                                        isDialog = false
                                        isInput.value = false
                                        isRefresh.value = true
                                        context.showMsg(it.data)
                                    }
                                    is ResultState.Failure -> {
                                        isDialog = false
                                        context.showMsg(it.msg.toString())
                                    }
                                    ResultState.Loading -> {
                                        isDialog = true
                                    }
                                }
                            }
                        }
                    }) {
                        Text(text = "Save")
                    }
                }
            },
            buttons = {

            }
        )
    }

    if(isRefresh.value) {
        isRefresh.value = false
        viewModel.getItems()
    }

    if (isUpdate.value)
        Update(
            viewModel.updateData.value,
            isUpdate,
            viewModel,
            isRefresh
        )

    if (res.data.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(res.data, key = {
                it.key!!
            }) { user ->
                EachRow1(user = user, onUpdate = {
                    isUpdate.value = true
                    viewModel.setData(
                        FirestoreModelResponse(
                            key = user.key,
                            item = FirestoreModelResponse.FirestoreItem(
                                user.item?.title,
                                user.item?.description
                            )
                        )
                    )
                }) {
                    scope.launch {
                        viewModel.delete(user.key!!).collect {
                            when (it) {
                                is ResultState.Success -> {
                                    isDialog = false
                                    isRefresh.value = true
                                    context.showMsg(it.data)
                                }
                                is ResultState.Failure -> {
                                    isDialog = false
                                    context.showMsg(it.msg.toString())
                                }
                                ResultState.Loading -> {
                                    isDialog = true
                                }
                            }
                        }
                    }
                }
            }

        }
    }


    if (res.error.isNotEmpty())
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = res.error)
        }

    if (res.isLoading)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }


}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EachRow1(
    user: FirestoreModelResponse,
    onUpdate: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        elevation = 1.dp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        onClick = {
            onUpdate()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = user.item?.title!!, style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                IconButton(onClick = {
                    onDelete()
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "", tint = Color.Red)
                }
            }
            Text(
                text = user.item?.description!!, style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            )
        }
    }
}


@Composable
fun Update(
    item: FirestoreModelResponse,
    isDialog: MutableState<Boolean>,
    viewModel: FirestoreViewModel,
    isRefresh:MutableState<Boolean>
) {

    var title by remember { mutableStateOf(item.item?.title) }
    var description by remember { mutableStateOf(item.item?.description) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var progress by remember { mutableStateOf(false) }

    AlertDialog(onDismissRequest = { isDialog.value = false },
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "Update List", modifier = Modifier.padding(vertical = 10.dp))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(value = title!!, onValueChange = {
                    title = it
                },
                    placeholder = { Text(text = "Enter title") }
                )
                Spacer(modifier = Modifier.height(20.dp))
                TextField(value = description!!, onValueChange = {
                    description = it
                },
                    placeholder = { Text(text = "Enter description") }
                )
            }
        },
        buttons = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(onClick = {
                    scope.launch {
                        viewModel.update(
                            FirestoreModelResponse(
                                key = item.key,
                                item = FirestoreModelResponse.FirestoreItem(
                                    title,
                                    description,
                                )
                            )
                        ).collect {
                            when (it) {
                                is ResultState.Success -> {
                                    context.showMsg(it.data)
                                    isDialog.value = false
                                    progress = false
                                    isRefresh.value = true
                                }
                                is ResultState.Failure -> {
                                    context.showMsg(it.msg.toString())
                                    isDialog.value = false
                                    progress = false
                                }
                                ResultState.Loading -> {
                                    progress = true
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

    if (progress)
        CommonDialog()
}