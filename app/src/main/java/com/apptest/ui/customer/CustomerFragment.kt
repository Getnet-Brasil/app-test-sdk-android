package com.apptest.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apptest.R
import com.apptest.model.Methods
import com.apptest.ui.util.MethodsAdapter
import com.getnetpd.enums.client.GPDEnviroment
import kotlinx.android.synthetic.main.fragment_customer.*


class CustomerFragment : Fragment() {

    private lateinit var viewModel: CustomerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this,
                ViewModelProvider.NewInstanceFactory()).get(CustomerViewModel::class.java)

        val root = inflater.inflate (R.layout.fragment_customer, container, false)

        viewModel.getMethodsCustomer()
        initView()

        return root
    }

    private fun initView(){
        viewModel.listMethods.observe(viewLifecycleOwner){
            recyclerCustomer.layoutManager = LinearLayoutManager(context)
            val adapter = MethodsAdapter(it!!, this::onItemClick)
            recyclerCustomer.adapter = adapter
        }
    }

    private fun onItemClick(method: Methods) {
        val bundle = Bundle()
        bundle.putSerializable("method", method)
        bundle.putString("title", method.name)
        bundle.putSerializable("env", GPDEnviroment.valueOf(spEnv.selectedItem.toString()).toString())
        findNavController().navigate(R.id.action_nav_recurrence_customer_to_recurrenceCustomerFormFragment2, bundle)
    }
}