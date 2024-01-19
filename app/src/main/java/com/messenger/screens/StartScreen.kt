package com.messenger.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
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
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.messenger.navigation.CHAT_SCREEN
import com.messenger.navigation.REGISTER_SCREEN
import com.messenger.ui.theme.greenColor
import java.util.concurrent.TimeUnit

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
                        title = {
                            Text(
                                text = "Messenger MOOZ",
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
                VerificationUI(LocalContext.current, navHostController)
            }
        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationUI(context: Context, navHostController: NavHostController) {
    var phonenumber by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var verificationID by remember { mutableStateOf("") }
    val message = remember {
        mutableStateOf("")
    }

    var mAuth: FirebaseAuth = FirebaseAuth.getInstance();
    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            message.value = "Verification successful"
            Toast.makeText(context, "Verification successful..", Toast.LENGTH_SHORT).show()
        }

        override fun onVerificationFailed(e: FirebaseException) {
            message.value = "Fail to verify user : \n" + e.message
            Toast.makeText(context, "Verification failed..", Toast.LENGTH_SHORT).show()
            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(context, "Verification failed Invalid request", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(context, "Verification failed The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show()
            }
            else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                Toast.makeText(context, "Verification failed reCAPTCHA verification attempted with null Activity", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
            Log.d("TAG", "onCodeSent:$verificationId")
            verificationID = verificationId
            Log.d("errorr", verificationID)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = phonenumber,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            onValueChange = { phonenumber = it },
            placeholder = { Text(text = "Enter your phone number") },
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
                if (TextUtils.isEmpty(phonenumber)) {
                    Toast.makeText(context, "Please enter phone number..", Toast.LENGTH_SHORT)
                        .show()
                }
                else{
                    Toast.makeText(context, "ok", Toast.LENGTH_SHORT)
                        .show()
                    sendVerificationCode(phonenumber, mAuth, context as Activity, callbacks)

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
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(context, "Please enter CODE..", Toast.LENGTH_SHORT).show()
                } else {
                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationID, code
                    )

                    signInWithPhoneAuthCredential(
                        credential,
                        mAuth,
                        context as Activity,
                        context,
                        message,
                        navHostController
                    )
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
            text = message.value,
            style = TextStyle(color = greenColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        )
    }
}

private fun signInWithPhoneAuthCredential(
    credential: PhoneAuthCredential,
    auth: FirebaseAuth,
    activity: Activity,
    context: Context,
    message: MutableState<String>,
    navHostController: NavHostController
) {
    auth.signInWithCredential(credential)
        .addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                message.value = "Verification successful"
                val user = task.result?.user!!
                Toast.makeText(context, "uid = ${user.uid}", Toast.LENGTH_SHORT).show()
                navHostController.currentBackStackEntry?.savedStateHandle?.set(
                    "useruid",
                    user.uid
                )
                navHostController.currentBackStackEntry?.savedStateHandle?.set(
                    "usernumber",
                    user.phoneNumber
                )
                navHostController.navigate(REGISTER_SCREEN)
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(
                        context,
                        "Verification failed.." + (task.exception as FirebaseAuthInvalidCredentialsException).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
}


private fun sendVerificationCode(
    number: String,
    auth: FirebaseAuth,
    activity: Activity,
    callbacks: OnVerificationStateChangedCallbacks
) {
    val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(number)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(activity)
        .setCallbacks(callbacks)
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}