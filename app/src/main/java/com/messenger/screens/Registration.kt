package com.messenger.screens

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.messenger.Data.User
import com.messenger.navigation.HOME_SCREEN
import com.messenger.navigation.REGISTER_SCREEN
import com.messenger.ui.theme.greenColor

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Registration(navHostController: NavHostController) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color  = Color.White
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "REGISTRATION",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            textAlign = TextAlign.Center,
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = greenColor,
                        titleContentColor = Color.White,
                    ),
                )
            }
        ) {
            RegistrationUI(LocalContext.current, navHostController)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationUI(context: Context, navHostController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    val useruid =
        navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("useruid") ?: String()
    val usernumber =
        navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("usernumber") ?: String()

    var database = Firebase.database.reference
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = { name = it },
            placeholder = { Text(text = "Enter your name") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFEDF2F6),
                cursorColor = Color.Black,
                disabledLabelColor = Color(0xff76a9ff),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = surname,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = { surname = it },
            placeholder = { Text(text = "Enter your surname") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFEDF2F6),
                cursorColor = Color.Black,
                disabledLabelColor = Color(0xff76a9ff),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(surname)) {
                    Toast.makeText(context, "Please enter name and surname..", Toast.LENGTH_SHORT).show()
                } else {


                    addToRealTimeDatabase(
                        name,
                        surname,
                        useruid,
                        usernumber,
                        database,
                        navHostController,
                        context
                    )

                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(color = greenColor, shape = RoundedCornerShape(5.dp))
        ) {
            Text(
                text = "Register",
                modifier = Modifier.padding(8.dp),
                style = TextStyle(fontSize = 20.sp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

    }
}

private fun addToRealTimeDatabase(
    name: String,
    surname: String,
    uid: String,
    phonenumber: String,
    database: DatabaseReference,
    navHostController: NavHostController,
    context: Context
) {
    val user = User(
        name, surname, uid, phonenumber
    )
    database.child("users").child(user.uid!!).setValue(user).addOnCompleteListener {
        if(it.isSuccessful){
            navHostController.currentBackStackEntry?.savedStateHandle?.set(
                "useruidchat",
                user.uid
            )
            Toast.makeText(context, "Sussessfully added user", Toast.LENGTH_SHORT).show()
            navHostController.navigate(HOME_SCREEN)
        }
        if(it.isCanceled){
            Toast.makeText(context, "Fail adding user", Toast.LENGTH_SHORT).show()
        }
    }
}