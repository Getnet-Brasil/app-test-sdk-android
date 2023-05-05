package com.apptest.ui.util

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apptest.R


class MethodsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txtMethod : TextView = itemView.findViewById(R.id.txtMethod)
}