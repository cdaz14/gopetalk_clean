package com.example.gopetalk_clean.ui.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gopetalk_clean.R
import com.example.gopetalk_clean.adapter.ChannelsAdapter
import com.example.gopetalk_clean.presentation.viewmodel.ChannelUiState
import com.example.gopetalk_clean.presentation.viewmodel.ChannelViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChannelFragment : Fragment(R.layout.fragment_channels) {

    private val viewModel: ChannelViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChannelsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_channels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView_channels)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ChannelsAdapter(emptyList()) { channelName ->
            Toast.makeText(requireContext(), "Selected: $channelName", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderState(state)
                }
            }
        }

        // Llamar a la carga de canales
        viewModel.loadChannels()
    }

    private fun renderState(state: ChannelUiState) {
        when {
            state.isLoading -> {
                // Aquí podrías mostrar un ProgressBar si lo agregas al layout
            }
            state.errorMessage != null -> {
                Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_LONG).show()
            }
            state.channels.isNotEmpty() -> {
                adapter.updateData(state.channels)
            }
        }
    }
}
