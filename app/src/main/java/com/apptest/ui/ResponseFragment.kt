package com.apptest.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apptest.R
import kotlinx.android.synthetic.main.fragment_response.*


class ResponseFragment : Fragment() {
    private var arg: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(arguments != null) {
            arg = arguments?.get("response") as String
            Log.d("TAG", "response: ${arg}")
        }
        return inflater.inflate(R.layout.fragment_response, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btClose.setOnClickListener {
            findNavController().popBackStack()
        }
        tvResponse.text = arg?.let { formatString(it) }
    }

    fun formatString(text: String): String? {
        val json = StringBuilder()
        var indentString = ""
        for (i in 0 until text.length) {
            val letter = text[i]
            when (letter) {
                '{', '[' -> {
                    json.append(
                        """
                        
                        $indentString$letter
                        
                        """.trimIndent()
                    )
                    indentString = indentString + "\t"
                    json.append(indentString)
                }
                '}', ']' -> {
                    indentString = indentString.replaceFirst("\t".toRegex(), "")
                    json.append(
                        """
                        
                        $indentString$letter
                        """.trimIndent()
                    )
                }
                ',' -> json.append(
                    """
                    $letter
                    $indentString
                    """.trimIndent()
                )
                else -> json.append(letter)
            }
        }
        return json.toString()
    }
}