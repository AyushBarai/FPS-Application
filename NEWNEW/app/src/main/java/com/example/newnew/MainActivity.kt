package com.example.newnew
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Telephony
import android.telecom.TelecomManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {
    lateinit var otp: EditText
    lateinit var otpotp: EditText
    lateinit var text: EditText
    lateinit var text2: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("In Main")
            if (ActivityCompat.checkSelfPermission(
                    this, (Manifest.permission.ANSWER_PHONE_CALLS)
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.ANSWER_PHONE_CALLS),
                    111
                )
            }
        receive()
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    @Suppress("OPT_IN_USAGE")
    private fun receive() {
        text = findViewById(R.id.text)
        text2 = findViewById(R.id.text2)
        otp = findViewById(R.id.otp)
        otpotp = findViewById(R.id.otpotp)

        val br = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onReceive(p0: Context?, p1: Intent?) {
                println("Messaged Recived")
                val tm = getSystemService(TELECOM_SERVICE) as TelecomManager
                GlobalScope.launch(Dispatchers.Main) {
                    for (sms in Telephony.Sms.Intents.getMessagesFromIntent(p1)) {
                        val otpche = Pattern.compile(("OTP"))
                        val oTPmatcher = otpche.matcher((sms.messageBody))
                        val otppatter = Pattern.compile("(|^)\\d{6}")
                        val matcher = otppatter.matcher(sms.messageBody)
                        runOnUiThread {
                            text.setText(sms.originatingAddress)
                            text2.setText(sms.displayMessageBody)
                            if (matcher.find()){
                                if(oTPmatcher.find()) {
                                    otpotp.setText(oTPmatcher.group(0))
                                    otp.setText(matcher.group(0))
                                    if (ActivityCompat.checkSelfPermission(
                                            this@MainActivity,
                                            Manifest.permission.READ_PHONE_STATE
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        ActivityCompat.requestPermissions(
                                            this@MainActivity,
                                            arrayOf(Manifest.permission.READ_PHONE_STATE),
                                            111
                                        )
                                    }
                                    if(tm.isInCall){
                                        println("TRUE Call Active")
                                        val intent = intent
                                        val number = intent.getStringExtra("number")

                                        if(number != null){
                                            val contact = exist(this@MainActivity, number)
                                            println(contact)
                                            if(!contact){
                                                @Suppress("DEPRECATION") val success = tm.endCall()
                                                println("Call Ended")
                                                println(success)
                                                // success == true if call was terminated.
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }


    @DelicateCoroutinesApi
    fun exist(context: Context, number: String?) : Boolean{
        /// number is the phone number
            println("number $number")
            val lookupUri: Uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number)
            )
            val mPhoneNumberProjection =
                arrayOf(
                    ContactsContract.PhoneLookup._ID,
                    ContactsContract.PhoneLookup.NUMBER,
                    ContactsContract.PhoneLookup.DISPLAY_NAME
                )

            val cur: Cursor? = context.contentResolver.query(lookupUri, mPhoneNumberProjection, null, null, null)

            if (cur != null) {
                if (cur.moveToFirst()) {
                    cur.close()
                    println("Contact Exist")
                    return true
                }
            }
            println("Contact Doesn't Exist")
            cur?.close()
            return false
        }
}

