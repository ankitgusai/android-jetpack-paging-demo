package com.ankit.workdaytest.ui.home

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ankit.workdaytest.R
import com.ankit.workdaytest.databinding.FragmentNasaImagesBinding
import com.ankit.workdaytest.ui.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Landing fragment. Displays search ability with FAB, loads images based on user query
 */
class NASAImagesFragment : Fragment() {
    private var _binding: FragmentNasaImagesBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //UI setup
        _binding = FragmentNasaImagesBinding.inflate(inflater, container, false)

        val pagingAdapter = SearchItemAdapter(requireContext()) {
            sharedViewModel.selectItem(it)
            findNavController().navigate(R.id.imageDetailFragment)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = pagingAdapter


        //UI Listeners
        binding.fabSearch.setOnClickListener {
            binding.fabSearch.animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(300)
                .withEndAction { // Hide the FAB button
                    binding.fabSearch.visibility = View.GONE
                    // Display the EditText with animation
                    binding.etSearch.visibility = View.VISIBLE
                    binding.etSearch.requestFocus()
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm!!.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)

                }
                .start()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {

                binding.fabSearch.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300)
                    .withStartAction { binding.fabSearch.visibility = View.VISIBLE }
                    .withEndAction { binding.etSearch.visibility = View.GONE }
                    .start()
                if (!TextUtils.isEmpty(binding.etSearch.text.trim())) {
                    sharedViewModel.setSearchQuery(binding.etSearch.text.trim().toString())
                }
            }
            return@setOnEditorActionListener false; // Let the system handle the event
        }


        binding.btRetry.setOnClickListener {
            pagingAdapter.retry()
        }

        //Easy Adapter state handling (loading, loaded, error empty etc)
        pagingAdapter.addLoadStateListener { loadState ->
            // Show loading spinner during initial load or refresh.
            binding.pbLoading.isVisible = loadState.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.btRetry.isVisible = loadState.source.refresh is LoadState.Error
            // Show error message.
            val errorState = loadState.source.refresh as? LoadState.Error
            binding.tvMessage.isVisible = errorState != null
            binding.tvMessage.text = errorState?.error?.localizedMessage

            if (errorState == null && loadState.source.append.endOfPaginationReached && pagingAdapter.itemCount < 1){
                binding.tvMessage.isVisible = true
                binding.tvMessage.text = "No result for your query"
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.searchResults.collectLatest { pagingData ->
                    pagingAdapter.submitData(pagingData)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // to avoid memory leaks.
    }
}