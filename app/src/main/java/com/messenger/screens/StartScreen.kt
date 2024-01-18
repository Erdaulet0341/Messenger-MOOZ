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
import androidx.compose.material3.TopAppBar
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
import com.messenger.navigation.HOME_SCREEN
import com.messenger.ui.theme.greenColor

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StartScreen(navHostController: NavHostController) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color  = Color.White
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        modifier = Modifier.background(color = greenColor),
                        title = {
                            Text(
                                text = "WELCOME",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = greenColor
                            )
                        }
                    )
                }
            ) {
                PhoneVerificationUI(LocalContext.current, navHostController)
            }
        }

}

@Composable
fun PhoneVerificationUI(context: Context, navHostController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("4") }
    var verificationID by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = { email = it },
            placeholder = { Text(text = "Enter your email address") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(context, "Please enter email address..", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val email = email
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(color = greenColor, shape = RoundedCornerShape(5.dp))
        ) {
            Text(
                text = "Generate CODE",
                modifier = Modifier.padding(8.dp),
                style = TextStyle(fontSize = 20.sp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = code,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { code = it },
            placeholder = { Text(text = "Enter your CODE") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(context, "Please enter CODE..", Toast.LENGTH_SHORT).show()
                } else {
                    navHostController.popBackStack()
                    navHostController.navigate(HOME_SCREEN)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(color = greenColor, shape = RoundedCornerShape(5.dp))
        ) {
            Text(
                text = "Verify CODE",
                modifier = Modifier.padding(8.dp),
                style = TextStyle(fontSize = 20.sp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = message,
            style = TextStyle(color = greenColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        )
    }
}