package com.messenger.screens


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.messenger.R
import com.messenger.data.Message
import com.messenger.data.User
import com.messenger.messengerElements.IconComponentDrawable
import com.messenger.messengerElements.SpacerWidth
import com.messenger.navigation.HOME_SCREEN
import com.messenger.ui.theme.Gray
import com.messenger.ui.theme.Gray400
import com.messenger.ui.theme.Line
import com.messenger.ui.theme.ReceiverColor
import com.messenger.ui.theme.SenderColor
import com.messenger.ui.theme.gradient1
import com.messenger.ui.theme.gradient2
import com.messenger.ui.theme.gradient3
import com.messenger.ui.theme.gradient4
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ChatScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val sender = retrieveUserData(context, "userdata")
    val receiver = retrieveUserData(context, "receiverUser")

    val senderroom = sender!!.uid+receiver!!.uid

    var database = Firebase.database.reference

    var messageList by remember { mutableStateOf<List<Message>>(emptyList()) }
    val databaseReference: DatabaseReference =
        database.child("chats").child(senderroom).child("messages")

    databaseReference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newMessageList = mutableListOf<Message>()

            for (dataSnapshot in snapshot.children) {
                val message = dataSnapshot.getValue(Message::class.java)
                newMessageList.add(message!!)
            }
            messageList = newMessageList
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
                UserNameRow(
                    user = receiver,
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
                        bottom = 75.dp
                    )
                ) {
                    items(messageList, key = { it.hashCode() }) {
                        ChatRow(message = it, sender!!.uid!!)
                    }
                }
            }
        }

        CustomTextField(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .align(BottomCenter),
            useruid = sender!!.uid!!,
            receiveruid = receiver!!.uid!!,
            database = database
        )
    }

}

fun saveUserData(context: Context, userData: User, preName:String ) {
    val preferences = context.getSharedPreferences(preName, Context.MODE_PRIVATE)
    val json = Json.encodeToString(userData)
    preferences.edit {
        putString("userData", json)
    }
}

fun retrieveUserData(context: Context, preName:String ): User?{
    val preferences = context.getSharedPreferences(preName, Context.MODE_PRIVATE)
    val json = preferences.getString("userData", null)
    return if (json != null) {
        Json.decodeFromString(json)
    } else {
        null
    }
}

@Composable
fun ChatRow(
    message: Message,
    senderuid:String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (message.id != senderuid) Alignment.Start else Alignment.End
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (message.id != senderuid) ReceiverColor else SenderColor,
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
                    text = message.message!!,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .widthIn(0.dp, 232.dp)
                )
                Text(
                    text = message.time!!,
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
    modifier: Modifier = Modifier,
    useruid: String,
    receiveruid: String,
    database: DatabaseReference,
    ) {

    var message by remember { mutableStateOf("") }

    TextField(
        value = message, onValueChange = {message = it },
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
            IconButton(onClick = {
                if(message.isNotEmpty()){
                    Toast.makeText(context, "Send!!", Toast.LENGTH_SHORT)
                        .show()

                    message = message.replace("\\s+$".toRegex(), "")
                    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
                    val currentTime = LocalTime.now()

                    val formattedTime = currentTime.format(formatter)
                    val oneMessage = Message(message, formattedTime, useruid)
                    val senderroom = useruid+receiveruid
                    val receiverroom = receiveruid+useruid

                    val senderRef = database.child("chats")
                        .child(senderroom)
                        .child("messages")
                        .push()

                    val receiverRef = database.child("chats")
                        .child(receiverroom)
                        .child("messages")
                        .push()

                    senderRef.setValue(oneMessage).addOnCompleteListener { senderTask ->
                        if (senderTask.isSuccessful) {
                            receiverRef.setValue(oneMessage).addOnCompleteListener {
                                Toast.makeText(context, "Sussessfully added", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Failer added", Toast.LENGTH_SHORT).show()
                        }
                    }

                    message = ""
                }
            }
            ) {
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
    user: User,
    navHostController: NavHostController
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            val context: Context = LocalContext.current

            IconButton(onClick = {

                navHostController.currentBackStackEntry?.savedStateHandle?.set(
                    "useruidchat",
                    user.uid
                )
                navHostController.navigate(HOME_SCREEN){
                    popUpTo("currentScreen") {
                        inclusive = true
                    }

                }
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
            val gradientList = listOf(gradient1, gradient2, gradient3, gradient4)

            var iconText = "${user.name!!.first()}${user.surname!!.first()}".uppercase(Locale.ROOT)
            val randomGradient = gradientList.random()
            val gradientBrush = Brush.linearGradient(randomGradient)

            IconComponentDrawable(text = iconText, size = 45.dp, backgroundColor = gradientBrush, textColor = Color.White)

            SpacerWidth()
            Column {

                Row {
                    Text(
                        text = user.name!!, style = TextStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )

                    Text(
                        text = user.surname!!, style = TextStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
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