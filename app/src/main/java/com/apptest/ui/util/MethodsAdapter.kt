package com.apptest.ui.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apptest.R
import com.apptest.model.Methods

class MethodsAdapter(private val listMethods: List<Methods>, private val onItemClick: (Methods) -> Unit) : RecyclerView.Adapter<MethodsViewHolder>() {

    override fun onBindViewHolder(holder: MethodsViewHolder, position: Int) {
        val model = listMethods[position]
        holder.txtMethod.text = model.name

        holder.itemView.setOnClickListener {
            onItemClick(model)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MethodsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MethodsViewHolder(inflater.inflate(R.layout.item_methods_list, parent, false))
    }

    override fun getItemCount(): Int {
        return listMethods.size
    }
}
