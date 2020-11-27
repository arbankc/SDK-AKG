package com.akggame.akg_sdk.baseextend

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.akggame.akg_sdk.dao.api.model.request.FacebookAuthRequest
import com.akggame.akg_sdk.dao.api.model.response.CurrentUserResponse
import com.akggame.akg_sdk.util.Constants
import com.orhanobut.hawk.Hawk
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

open class BaseActivity : AppCompatActivity() {

    private var accessToken: String? = null
    var dialogShow: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialogShow = Dialog(this)
        dialogShow!!.requestWindowFeature(Window.FEATURE_NO_TITLE)

        hideKeyboard()


    }

    private fun validateExpiredLogin() {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())

    }

    fun getDataUpsert(): FacebookAuthRequest {
        return Hawk.get(Constants.DATA_UPSERT)
    }


    fun getDataLogin(): CurrentUserResponse {
        return Hawk.get(Constants.DATA_LOGIN)
    }

    fun convertDate(getDate: String?): String {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val output = SimpleDateFormat("dd/MM/yyyy")

        var d: Date? = null
        try {
            d = input.parse(getDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return output.format(d)
    }

    fun showDialogPicker(editText: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = let {
            DatePickerDialog(it, { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                editText.setText("$year-$monthOfYear-$dayOfMonth")

            }, year, month, day)
        }

        dpd.show()
    }


    private fun getDate(timestamp: Long): String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp * 1000L
        return DateFormat.format("dd-MM-yyyy", calendar).toString()
    }

    open fun hideKeyboard() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    fun showView(view: View) {
        view.visibility = View.VISIBLE
    }


    fun hideView(view: View) {
        view.visibility = View.GONE
    }

    fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastErrorSomething() {
        Toast.makeText(this, "Terjadi kesalahan pada server/jaringan", Toast.LENGTH_SHORT).show()
    }


    fun setToolbar(toolbar: Toolbar, message: String?) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = message
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }


    fun setToolbarMain(toolbar: Toolbar?, message: String?) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = message
    }


    fun intentPage(classTarget: Class<*>) {
        val intent = Intent(this, classTarget)
        startActivity(intent)
    }

    fun intentPageData(classTarget: Class<*>): Intent {
        return Intent(this, classTarget);
    }

    fun intentPageFlags(classTarget: Class<*>) {
        val intent = Intent(this, classTarget)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encodeBase64(value: String?) {
        val encodedString =
            Base64.getEncoder().withoutPadding().encodeToString(value?.toByteArray())
        println(encodedString)
    }
}