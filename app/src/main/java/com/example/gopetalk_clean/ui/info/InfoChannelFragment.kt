package com.example.gopetalk_clean.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gopetalk_clean.R
import com.example.gopetalk_clean.adapter.ChannelsAdapter
import com.example.gopetalk_clean.databinding.FragmentInfoChannelBinding
import com.example.gopetalk_clean.presentation.viewmodel.ChannelViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InfoChannelFragment : Fragment(R.layout.fragment_info_channel) {

    private var _binding: FragmentInfoChannelBinding? = null
    private val binding get() = _binding!!

    private val channelViewModel: ChannelViewModel by viewModels()
    private val channelUsersViewModel: ChannelUsersViewModel by viewModels()

    private lateinit var adapter: ChannelsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()

        //  Observar lista de canales desde uiState del ChannelViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            channelViewModel.uiState.collectLatest { state ->
                adapter.updateData(state.channels)
            }
        }

        // 🔹 Observar el estado de usuarios en el canal
        viewLifecycleOwner.lifecycleScope.launch {
            channelUsersViewModel.uiState.collectLatest { state ->
                binding.textChannel.text = state.channelName ?: "Canal"
                binding.textUsersOnChannel.text = "${state.userCount} usuarios"
            }
        }

        // 🔹 Botón para desplegar/ocultar lista de canales
        binding.btnSeeChannels.setOnClickListener {
            binding.recyclerChannels.visibility =
                if (binding.recyclerChannels.isGone) View.VISIBLE else View.GONE

            if (binding.recyclerChannels.isVisible) {
                channelViewModel.fetchChannels()
            }
        }

        // 🔹 Botón desconectar
        binding.btnDisconnect.setOnClickListener {
            channelViewModel.disconnectChannel()
            binding.textChannel.text = "Sin canal"
            binding.textUsersOnChannel.text = "0 usuarios"
        }
    }

    private fun setupRecycler() {
        adapter = ChannelsAdapter(emptyList()) { selectedChannel ->
            binding.textChannel.text = selectedChannel

            channelUsersViewModel.fetchChannelUsers(
                channelId = selectedChannel,
                channelName = selectedChannel
            )

            binding.recyclerChannels.visibility = View.GONE
        }

        binding.recyclerChannels.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerChannels.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
