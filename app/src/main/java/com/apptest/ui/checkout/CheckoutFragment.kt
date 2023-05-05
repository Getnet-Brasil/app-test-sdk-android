package com.apptest.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apptest.R
import com.getnetpd.enums.client.GPDEnviroment
import kotlinx.android.synthetic.main.fragment_checkout.*


class CheckoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btCallCheckoutForm.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("env", GPDEnviroment.valueOf(spEnvCheckout.selectedItem.toString()).toString())
            findNavController().navigate(R.id.action_checkoutFragment_to_checkoutFormFragment, bundle)
        }
    }
}