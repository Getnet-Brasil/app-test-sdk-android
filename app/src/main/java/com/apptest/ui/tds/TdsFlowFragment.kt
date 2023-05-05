package com.apptest.ui.tds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.apptest.R
import com.getnetpd.enums.client.GPDEnviroment
import kotlinx.android.synthetic.main.fragment_tds_flow2.*

class TdsFlowFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tds_flow2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btCallCheckoutForm.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("env", GPDEnviroment.valueOf(spEnvCheckout.selectedItem.toString()).toString())
            findNavController().navigate(R.id.action_tdsFlowFragment_to_nav_tds, bundle)
        }
    }
}