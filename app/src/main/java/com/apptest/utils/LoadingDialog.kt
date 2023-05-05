package com.apptest.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import com.apptest.R

class LoadingDialog(myActivity: Activity) {

    private var activity: Activity? = null
    private var alertDialog: Dialog? = null

    init {
        activity = myActivity
    }

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)
        val layoutInflater = activity?.layoutInflater

        builder.setView(layoutInflater?.inflate(R.layout.loading_dialog, null))
        builder.setCancelable(false)

        alertDialog = builder.create()
        alertDialog?.show()
    }

    fun dismissLoadingDialog() {
        alertDialog?.dismiss()
    }
}