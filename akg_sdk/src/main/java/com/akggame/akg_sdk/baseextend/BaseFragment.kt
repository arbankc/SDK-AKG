package com.akggame.akg_sdk.baseextend

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

open class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideKeyboard()
    }

    open fun hideKeyboard() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    fun showView(view: View) {
        view.visibility = View.VISIBLE
    }

    fun showDialogPicker(editText: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = activity?.let {
            DatePickerDialog(it, { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                editText.setText("$year-$monthOfYear-$dayOfMonth")

            }, year, month, day)
        }

        dpd?.show()
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

    fun hideView(view: View) {
        view.visibility = View.GONE
    }


    fun showToast(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastErrorSomething() {
        Toast.makeText(activity, "Terjadi kesalahan pada server/jaringan", Toast.LENGTH_SHORT)
            .show()
    }


    fun intentPage(classTarget: Class<*>) {
        val intent = Intent(activity, classTarget)
        startActivity(intent)
    }

    fun intentPageData(classTarget: Class<*>): Intent {
        return Intent(activity, classTarget)
    }


    fun intentPageFlags(classTarget: Class<*>) {
        val intent = Intent(activity, classTarget)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}