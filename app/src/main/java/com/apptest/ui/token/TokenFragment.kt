package com.apptest.ui.token

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
import kotlinx.android.synthetic.main.fragment_token.*

class TokenFragment : Fragment() {
    private lateinit var viewModel: TokenViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this,
                ViewModelProvider.NewInstanceFactory()).get(TokenViewModel::class.java)

        val root = inflater.inflate (R.layout.fragment_token, container, false)

        viewModel.getMethodsToken()
        initView()

        return root
    }

    fun initView(){
        viewModel.listMethods.observe(viewLifecycleOwner){
            recyclerToken.layoutManager = LinearLayoutManager(context)
            val adapter = MethodsAdapter(it!!, this::onItemClick)
            recyclerToken.adapter = adapter
        }
    }

    private fun onItemClick(method: Methods) {
        val bundle = Bundle()
        bundle.putSerializable("method", method)
        bundle.putString("title", method.name)
        bundle.putSerializable("env", GPDEnviroment.valueOf(spEnvSubs.selectedItem.toString()).toString())
        findNavController().navigate(R.id.action_tokenFragment_to_tokenFormFragment, bundle)
    }
}