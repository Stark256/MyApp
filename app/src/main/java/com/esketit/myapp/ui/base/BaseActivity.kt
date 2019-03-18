package com.esketit.myapp.ui.base

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.esketit.myapp.R

abstract class BaseActivity: AppCompatActivity(){
    
    private lateinit var progressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.progressDialog = AlertDialog.Builder(this)
            .setView(R.layout.dialog_progress)
            .setCancelable(false)
            .create()
    }

//    override fun onResume() {
//        super.onResume()
//        updateUI()
//    }

    fun showError(message: String){
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->  dialog.dismiss() }
            .show()
    }

    fun setError(layout: TextInputLayout, error: String?) {
        layout.isErrorEnabled = error != null
        layout.error = error
    }


    fun setSupportActionBar(toolbar: Toolbar, isHomeAsUpEnebled: Boolean, showTitle: Boolean){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(isHomeAsUpEnebled)
        supportActionBar?.setDisplayShowTitleEnabled(showTitle)
    }

    fun setToolbarTitle(title: String){
        supportActionBar?.title = title
    }

    fun showProgressDialog(){ progressDialog.show() }
    fun hideProgressDialog(){ progressDialog.dismiss() }

//    fun updateUI(){}
   // abstract fun updateStrings(){}
}
