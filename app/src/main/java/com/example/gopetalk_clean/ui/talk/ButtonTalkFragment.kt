package com.example.gopetalk_clean.ui.talk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gopetalk_clean.R
import com.example.gopetalk_clean.databinding.FragmentButtonTalkBinding
import dagger.hilt.android.AndroidEntryPoint


class ButtonTalkFragment : Fragment(R.layout.fragment_button_talk) {

    private var _binding: FragmentButtonTalkBinding? = null
    private val binding get() = _binding!!

    private val buttonTalkViewModel: ButtonTalkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentButtonTalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
