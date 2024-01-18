package com.nameisjayant.projects.chat.components.screens


import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.messenger.R
import com.messenger.messengerElements.IconComponentDrawable
import com.messenger.messengerElements.IconComponentImageVector
import com.messenger.messengerElements.SpacerWidth
import com.messenger.testData.Chat
import com.messenger.testData.Person
import com.messenger.testData.chatList
import com.messenger.ui.theme.Gray
import com.messenger.ui.theme.Gray400
import com.messenger.ui.theme.Line
import com.messenger.ui.theme.SenderColor
import com.messenger.ui.theme.ReceiverColor

@Composable
fun ChatScreen(
    navHostController: NavHostController
) {

    var message by remember { mutableStateOf("") }
    val data =
        navHostController.previousBackStackEntry?.savedStateHandle?.get<Person>("data") ?: Person()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            UserNameRow(
                person = data,
                modifier = Modifier.padding(top = 12.dp, start = 8.dp, bottom = 8.dp),
                navHostController = navHostController
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 1.dp, color = Line,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                LazyColumn(
                    modifier = Modifier.padding(
                        start = 8.dp,
                        top = 15.dp,
                        end = 8.dp,
                        bottom = 15.dp
                    )
                ) {
                    items(chatList, key = { it.id }) {
                        ChatRow(chat = it)
                    }
                }
            }
        }

        CustomTextField(
            text = message, onValueChange = { message = it },
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .align(BottomCenter)
        )
    }

}

@Composable
fun ChatRow(
    chat: Chat
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (chat.direction) Alignment.Start else Alignment.End
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (chat.direction) ReceiverColor else SenderColor,
                    RoundedCornerShape(100.dp)
                )
                .padding(4.dp),
            contentAlignment = Center
        ) {

            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = chat.message,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .widthIn(0.dp, 232.dp)
                )
                Text(
                    text = chat.time,
                    style = TextStyle(
                        color = Gray,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(start = 5.dp, top = 15.dp)
                )
            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    text: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = text, onValueChange = { onValueChange(it) },
        placeholder = {
            Text(
                text = stringResource(R.string.type_message),
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xFF9DB7CB)
                ),
                textAlign = TextAlign.Center
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Gray400,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        leadingIcon = {
            val context: Context = LocalContext.current
            IconButton(onClick = { Toast.makeText(context, "Add!!", Toast.LENGTH_SHORT)
                .show()}) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "send"
                )
            }},

        trailingIcon = {
            val context: Context = LocalContext.current
            IconButton(onClick = { Toast.makeText(context, "Send!!", Toast.LENGTH_SHORT)
                .show()}) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = "send"
                )
            }
        },

        modifier = modifier.fillMaxWidth(),
        shape = CircleShape
    )

}

@Composable
fun UserNameRow(
    modifier: Modifier = Modifier,
    person: Person,
    navHostController: NavHostController
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            val context: Context = LocalContext.current

            IconButton(onClick = {
                navHostController.popBackStack()
                Toast.makeText(
                    context, "Back!!", Toast.LENGTH_SHORT
                )
                    .show()}) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowLeft,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(1.5f),
                    contentDescription = "send"
                )
            }

            IconComponentDrawable(icon = R.drawable.img, size = 42.dp)
            SpacerWidth()
            Column {
                Text(
                    text = person.name, style = TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = stringResource(R.string.online), style = TextStyle(
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }

}