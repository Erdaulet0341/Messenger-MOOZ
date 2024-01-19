package com.messenger.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.messenger.data.User
import com.messenger.messengerElements.IconComponentDrawable
import com.messenger.messengerElements.SpacerHeight
import com.messenger.messengerElements.SpacerWidth
import com.messenger.navigation.CHAT_SCREEN
import com.messenger.ui.theme.Gray
import com.messenger.ui.theme.Line
import com.messenger.ui.theme.gradient1
import com.messenger.ui.theme.gradient2
import com.messenger.ui.theme.gradient3
import com.messenger.ui.theme.gradient4
import com.messenger.R
import java.util.Locale

@Composable
fun HomeScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val user = retrieveUserData(context, "userdata")

    var database = Firebase.database.reference

    var userList by remember { mutableStateOf<List<User>>(emptyList()) }
    if(user != null){
        LaunchedEffect(user.uid) {
            fetchUsers(database, context) { updatedUserList ->
                userList = updatedUserList
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            Headers()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.White, RoundedCornerShape(
                            topStart = 30.dp, topEnd = 30.dp
                        )
                    )
            ) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    thickness = 1.dp, color = Line,
                )

                LazyColumn(
                    modifier = Modifier.padding(bottom = 15.dp, top = 30.dp)
                ) {
                    items(userList) {
                        UserEachRow(user = it) {
                            saveUserData(context, it, "receiverUser")
                            navHostController.navigate(CHAT_SCREEN)
                        }
                    }
                }
            }
        }

    }

}


@Composable
fun Headers (){
    var searchQuery by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp)
    ) {
        Header()
        SearchBar(onSearch = { newQuery ->
            searchQuery = newQuery
        })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var searchText by remember { mutableStateOf("") }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(end = 24.dp),
        value = searchText,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color(0xFFEDF2F6),
            cursorColor = Color.Black,
            disabledLabelColor = Color(0xff76a9ff),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        onValueChange = {
            searchText = it
            onSearch(it)
        },
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                IconButton(onClick = { searchText = "" }) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null
                    )
                }
            }
        },
        placeholder = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    modifier = Modifier
                        .size(20.dp)
                        .scale(1.5f),
                    tint = Color(0xFF9DB7CB)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search...", color = Color(0xFF9DB7CB))
            }
        },
    )
}

@Composable
fun UserEachRow(
    user: User,
    onClick: () -> Unit
) {
    val gradientList = listOf(gradient1, gradient2, gradient3, gradient4)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .noRippleEffect { onClick() }
            .padding(horizontal = 20.dp, vertical = 5.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    val iconText =
                        "${user.name!!.first()}${user.surname!!.first()}".uppercase(Locale.ROOT)
                    val randomGradient = gradientList.random()
                    val gradientBrush = Brush.linearGradient(randomGradient)

                    IconComponentDrawable(text = iconText, size = 60.dp, backgroundColor = gradientBrush, textColor = Color.White)
                    SpacerWidth()
                    Column {

                        Row {
                            Text(
                                text = user.name!!, style = TextStyle(
                                    color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                            )

                            Text(
                                text = user.surname!!, style = TextStyle(
                                    color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        SpacerHeight(5.dp)
                        Text(
                            text = "Okey", style = TextStyle(
                                color = Gray, fontSize = 14.sp
                            )
                        )
                    }

                }
                Text(
                    text = stringResource(R.string._12_23_pm), style = TextStyle(
                        color = Gray, fontSize = 12.sp
                    )
                )
            }
            Divider(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp), thickness = 1.dp, color = Line)
        }
    }

}



@Composable
fun Header() {
    Text(
        text = "Чаты",
        style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.W600),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@SuppressLint("UnnecessaryComposedModifier", "ModifierFactoryUnreferencedReceiver")
fun Modifier.noRippleEffect(onClick: () -> Unit) = composed {
    clickable(
        interactionSource = MutableInteractionSource(),
        indication = null
    ) {
        onClick()
    }
}

private fun fetchUsers(
    database: DatabaseReference,
    context: Context,
    onUsersFetched: (List<User>) -> Unit
) {
    val userList = ArrayList<User>()

    database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (userSnapshot in dataSnapshot.children) {
                val user = userSnapshot.getValue(User::class.java)
                user?.let { userList.add(it) }
            }
            if (userList.isNotEmpty()) {
                Toast.makeText(context, "Users loaded successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No users found", Toast.LENGTH_SHORT).show()
            }

            onUsersFetched(userList)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Toast.makeText(context, "Error fetching users", Toast.LENGTH_SHORT).show()
        }
    })
}