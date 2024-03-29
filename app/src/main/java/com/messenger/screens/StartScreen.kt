package com.messenger.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.messenger.navigation.HOME_SCREEN
import com.messenger.navigation.REGISTER_SCREEN
import com.messenger.ui.theme.greenColor
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StartScreen(navHostController: NavHostController) {

    val context = LocalContext.current
    val user = retrieveUserData(context, "userdata")

    if(user != null){
        navHostController.navigate(HOME_SCREEN)
    }
    else {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Мессенджер MOOZ",
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationUI(context: Context, navHostController: NavHostController) {
    var phonenumber by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var verificationID by remember { mutableStateOf("") }

    var mAuth: FirebaseAuth = FirebaseAuth.getInstance();
    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            Toast.makeText(context, "Проверка прошла успешно.", Toast.LENGTH_SHORT).show()
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(context, "Проверка не удалась.", Toast.LENGTH_SHORT).show()
            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(context, "Проверка не удалась. Неверный запрос.t", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(context, "Проверка не удалась. Превышена квота SMS для проекта.", Toast.LENGTH_SHORT).show()
            }
            else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                Toast.makeText(context, "Проверка не удалась. Попытка проверки reCAPTCHA с нулевой активностью.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
            verificationID = verificationId
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
            placeholder = { Text(text = "Введите свой номер телефона") },
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
                    Toast.makeText(context, "Пожалуйста, введите номер телефона..", Toast.LENGTH_SHORT)
                        .show()
                }
                else if(!isInternetConnected(context)){
                    Toast.makeText(context, "Пожалуйста, проверьте интернет подключения..", Toast.LENGTH_SHORT).show()
                }
                else{
                    sendVerificationCode(phonenumber, mAuth, context as Activity, callbacks)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(color = greenColor, shape = RoundedCornerShape(5.dp))
        ) {
            Text(
                text = "Сгенерировать КОД",
                modifier = Modifier.padding(8.dp),
                style = TextStyle(fontSize = 20.sp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = code,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { code = it },
            placeholder = { Text(text = "Введите ваш код") },
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
                    Toast.makeText(context, "Пожалуйста, введите КОД..", Toast.LENGTH_SHORT).show()
                }
                else if(!isInternetConnected(context)){
                    Toast.makeText(context, "Пожалуйста, проверьте интернет подключения..", Toast.LENGTH_SHORT).show()
                }
                else {
                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationID, code
                    )

                    signInWithPhoneAuthCredential(
                        credential,
                        mAuth,
                        context as Activity,
                        context,
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
                text = "Подтвердит КОД",
                modifier = Modifier.padding(8.dp),
                style = TextStyle(fontSize = 20.sp)
            )
        }
    }
}

private fun signInWithPhoneAuthCredential(
    credential: PhoneAuthCredential,
    auth: FirebaseAuth,
    activity: Activity,
    context: Context,
    navHostController: NavHostController
) {
    auth.signInWithCredential(credential)
        .addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
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
                        "Проверка не удалась." + (task.exception as FirebaseAuthInvalidCredentialsException).message,
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

fun isInternetConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    if (connectivityManager != null) {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }

    return false
}