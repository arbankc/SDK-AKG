package com.akggame.akg_sdk.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.*
import android.text.TextUtils
import android.util.Base64.URL_SAFE
import android.util.Base64.decode
import com.google.gson.Gson
import java.io.UnsupportedEncodingException
import java.util.Base64.Decoder


object DeviceUtil {

    fun getImei(cuntext: Context): String {
        val manager = cuntext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                cuntext,
                Manifest.permission.READ_PHONE_STATE
            ) === PackageManager.PERMISSION_GRANTED
        ) {
            return "0"
        } else {
            //            SessionManager.logout();
            Log.e("Get IMEI", "Tidak ada ijin untuk membuat id device")
            //Toast.makeText(cuntext, “Tidak ada ijin untuk membuat id device”, Toast.LENGTH_SHORT).show();
//            val intent = Intent(Intent.ACTION_MAIN)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            intent.addCategory(Intent.CATEGORY_HOME)
            makeRequest(cuntext)

            return ""
        }
    }



    fun decoded(JWTEncoded: String) : String {
         try {
             val split =
                 JWTEncoded.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
             Log.d("JWT_DECODED", "Header: " + getJson(split[0]))
             Log.d("JWT_DECODED", "Body: " + getJson(split[1]))
             return getJson(split[1])
         } catch (e: UnsupportedEncodingException) {
             //Error
             return ""
         }
     }

     private fun getJson(strEncoded: String): String {
         val decodedBytes = decode(strEncoded,URL_SAFE)
         return String(decodedBytes, Charsets.UTF_8)
     }

     fun getDeviceName():String{
         var manufacter = Build.MANUFACTURER
         var model = Build.MODEL

         if(model.startsWith(manufacter)){
             return capitalize(model)
         }

         return capitalize(manufacter) + " " + model;
     }

     private fun capitalize(str: String): String {
         if (TextUtils.isEmpty(str)) {
             return str
         }
         val arr = str.toCharArray()
         var capitalizeNext = true

         val phrase = StringBuilder()
         for (c in arr) {
             if (capitalizeNext && Character.isLetter(c)) {
                 phrase.append(Character.toUpperCase(c))
                 capitalizeNext = false
                 continue
             } else if (Character.isWhitespace(c)) {
                 capitalizeNext = true
             }
             phrase.append(c)
         }

         return phrase.toString()
     }

    private fun makeRequest(context: Context) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            201
        )
    }

}