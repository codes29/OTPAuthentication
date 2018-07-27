package com.tutorial.lws.otpauthentication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    internal lateinit var btnGenerateOTP: Button
    internal lateinit var btnSignIn: Button

    internal lateinit var etPhoneNumber: EditText
    internal lateinit var etOTP: EditText

    internal lateinit var phoneNumber: String
    internal lateinit var otp: String

    internal lateinit var auth: FirebaseAuth

    internal lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews()

        StartFirebaseLogin()

        btnGenerateOTP.setOnClickListener {
            phoneNumber = etPhoneNumber.text.toString()

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber, // Phone number to verify
                    60, // Timeout duration
                    TimeUnit.SECONDS, // Unit of timeout
                    this@MainActivity, // Activity (for callback binding)
                    mCallback)                      // OnVerificationStateChangedCallbacks
        }

        btnSignIn.setOnClickListener {
            otp = etOTP.text.toString()

            val credential = PhoneAuthProvider.getCredential(verificationCode!!, otp)

            SigninWithPhone(credential)
        }
    }

    private fun SigninWithPhone(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent =Intent(this@MainActivity, SignedIn::class.java)
                        startActivity(Intent(this@MainActivity, SignedIn::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Incorrect OTP", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun findViews() {
        btnGenerateOTP = findViewById(R.id.btn_generate_otp)
        btnSignIn = findViewById(R.id.btn_sign_in)

        etPhoneNumber = findViewById(R.id.et_phone_number)
        etOTP = findViewById(R.id.et_otp)
    }

    private fun StartFirebaseLogin() {

        auth = FirebaseAuth.getInstance()
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Toast.makeText(this@MainActivity, "verification completed", Toast.LENGTH_SHORT).show()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@MainActivity, "verification fialed", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(s: String?, forceResendingToken: PhoneAuthProvider.ForceResendingToken?) {
                super.onCodeSent(s, forceResendingToken)
                verificationCode = s
                Toast.makeText(this@MainActivity, "Code sent", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
