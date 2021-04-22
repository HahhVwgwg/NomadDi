package com.dataplus.tabyspartner.ui.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.FragmentNewsBinding
import com.dataplus.tabyspartner.model.ResultResponse
import com.dataplus.tabyspartner.ui.ui.profile.adapter.NewsAdapter

class NotificationsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_news, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.responseNews.observe(viewLifecycleOwner, {
            when (it) {
                is ResultResponse.Loading -> {}
                is ResultResponse.Success -> {
                    binding.list.adapter = NewsAdapter(it.data)
                }
                is ResultResponse.Error -> {
                    Toast.makeText(view.context, it.message, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNews()
    }
}