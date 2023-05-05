package com.apptest.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.apptest.R
import kotlinx.android.synthetic.main.fragment_options.*


class OptionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        btSaveOptions.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val preferences = context?.getSharedPreferences("APP-TEST-SDK", Context.MODE_PRIVATE)
        preferences?.edit()?.putString("SELLER_ID_HOMOLOG", edtSellerIdHomolog.text.toString())?.apply()
        preferences?.edit()?.putString("CLIENT_ID_HOMOLOG", edtClientIdHomolog.text.toString())?.apply()
        preferences?.edit()?.putString("CLIENT_SECRET_HOMOLOG", edtClientSecretHomolog.text.toString())?.apply()
        preferences?.edit()?.putString("SELLER_ID_PROD", edtSellerIdProd.text.toString())?.apply()
        preferences?.edit()?.putString("CLIENT_ID_PROD", edtClientIdProd.text.toString())?.apply()
        preferences?.edit()?.putString("CLIENT_SECRET_PROD", edtClientSecretProd.text.toString())?.apply()
        Toast.makeText(context, "Os valores foram salvos", Toast.LENGTH_SHORT).show()
    }

    private fun loadData() {
        val preferences = context?.getSharedPreferences("APP-TEST-SDK", Context.MODE_PRIVATE)
        edtSellerIdHomolog.setText(preferences?.getString("SELLER_ID_HOMOLOG", ""))
        edtClientIdHomolog.setText(preferences?.getString("CLIENT_ID_HOMOLOG", ""))
        edtClientSecretHomolog.setText(preferences?.getString("CLIENT_SECRET_HOMOLOG", ""))
        edtSellerIdProd.setText(preferences?.getString("SELLER_ID_PROD", ""))
        edtClientIdProd.setText(preferences?.getString("CLIENT_ID_PROD", ""))
        edtClientSecretProd.setText(preferences?.getString("CLIENT_SECRET_PROD", ""))
    }
}