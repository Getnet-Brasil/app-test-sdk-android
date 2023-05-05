package com.apptest.ui.vault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.apptest.R
import com.getnetpd.enums.client.GPDEnviroment
import kotlinx.android.synthetic.main.fragment_vault.*

class VaultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vault, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btCallVaultForm.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("env", GPDEnviroment.valueOf(spEnvVault.selectedItem.toString()).toString())
            findNavController().navigate(R.id.action_nav_vault_to_vaultFormFragment, bundle)
        }
    }
}