package com.apptest.ui.cards

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
import kotlinx.android.synthetic.main.fragment_cards.*

class CardsFragment : Fragment() {

    private lateinit var viewModel: CardsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this,
                ViewModelProvider.NewInstanceFactory()).get(CardsViewModel::class.java)

        val root = inflater.inflate (R.layout.fragment_cards, container, false)

        viewModel.getMethodsCustomer()
        initView()

        return root
    }

    private fun initView(){
        viewModel.listMethods.observe(viewLifecycleOwner){
            recyclerCards.layoutManager = LinearLayoutManager(context)
            val adapter = MethodsAdapter(it!!, this::onItemClick)
            recyclerCards.adapter = adapter
        }
    }

    private fun onItemClick(method: Methods) {
        val bundle = Bundle()
        bundle.putSerializable("method", method)
        bundle.putString("title", method.name)
        bundle.putSerializable("env", GPDEnviroment.valueOf(spEnv.selectedItem.toString()).toString())
        findNavController().navigate(R.id.action_cardsFragment_to_cardsFormFragment, bundle)
    }
}