package com.apptest.ui.payments.debit

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
import kotlinx.android.synthetic.main.fragment_debit.*

class DebitFragment : Fragment() {

    private lateinit var viewModel: DebitViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this,
                ViewModelProvider.NewInstanceFactory()).get(DebitViewModel::class.java)

        val root = inflater.inflate (R.layout.fragment_debit, container, false)

        viewModel.getMethodsCredit()
        initView()

        return root
    }

    private fun initView(){
        viewModel.listMethods.observe(viewLifecycleOwner){
            recyclerPaymentDebit.layoutManager = LinearLayoutManager(context)
            val adapter = MethodsAdapter(it!!, this::onItemClick)
            recyclerPaymentDebit.adapter = adapter
        }
    }

    private fun onItemClick(method: Methods) {
        val bundle = Bundle()
        bundle.putSerializable("method", method)
        bundle.putString("title", method.name)
        bundle.putSerializable("env", GPDEnviroment.valueOf(spEnv.selectedItem.toString()).toString())
        findNavController().navigate(R.id.action_debitFragment_to_debitFormFragment, bundle)
    }
}