package com.apptest.ui.payment3ds

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
import kotlinx.android.synthetic.main.fragment_payment3ds.*

class Payment3dsFragment : Fragment() {

    private lateinit var viewModel: Payment3dsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this,
                ViewModelProvider.NewInstanceFactory()).get(Payment3dsViewModel::class.java)

        val root = inflater.inflate (R.layout.fragment_payment3ds, container, false)

        viewModel.getMethodsPayment3ds()
        initView()

        return root
    }

    fun initView(){
        viewModel.listMethods.observe(viewLifecycleOwner){
            recyclerPayment3ds.layoutManager = LinearLayoutManager(context)
            val adapter = MethodsAdapter(it!!, this::onItemClick)
            recyclerPayment3ds.adapter = adapter
        }
    }

    private fun onItemClick(method: Methods) {
        val bundle = Bundle()
        bundle.putSerializable("method", method)
        bundle.putString("title", method.name)
        bundle.putSerializable("env", GPDEnviroment.valueOf(spEnvPayment3ds.selectedItem.toString()).toString())
        findNavController().navigate(R.id.action_nav_3ds_payment_to_payment3dsFormFragment, bundle)
    }
}