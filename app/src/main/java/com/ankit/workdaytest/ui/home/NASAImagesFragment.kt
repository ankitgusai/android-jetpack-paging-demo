package com.ankit.workdaytest.ui.home

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ankit.workdaytest.R
import com.ankit.workdaytest.databinding.FragmentNasaImagesBinding
import com.ankit.workdaytest.ui.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Landing fragment. Displays search ability with FAB, loads images based on user query
 */
class NASAImagesFragment : Fragment() {
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = DataBindingUtil.inflate<FragmentNasaImagesBinding>(layoutInflater, R.layout.fragment_nasa_images, container, false)

        binding.floatingActionButton.setOnClickListener {
            binding.floatingActionButton.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(300)
                .withEndAction(Runnable { // Hide the FAB button
                    binding.floatingActionButton.visibility = View.GONE

                    // Display the EditText with animation
                    binding.editTextText.visibility = View.VISIBLE
                    binding.editTextText.requestFocus()
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm!!.showSoftInput(binding.editTextText, InputMethodManager.SHOW_IMPLICIT)

                })
                .start()
        }

        binding.editTextText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {

                binding.floatingActionButton.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(300)
                    .withStartAction { binding.floatingActionButton.visibility = View.VISIBLE }
                    .withEndAction { binding.editTextText.visibility = View.GONE }
                    .start()
                if (!TextUtils.isEmpty(binding.editTextText.text.trim())) {
                    sharedViewModel.setSearchQuery(binding.editTextText.text.trim().toString())
                }
                return@setOnEditorActionListener false; // Consume the event
            }
            return@setOnEditorActionListener false; // Let the system handle the event
        }

        val pagingAdapter = SearchItemAdapter(requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = pagingAdapter


// Activities can use lifecycleScope directly, but Fragments should instead use
// viewLifecycleOwner.lifecycleScope.
        lifecycleScope.launch {
            sharedViewModel.searchResults.collectLatest { pagingData ->
                pagingAdapter.submitData(pagingData)
            }

        }

        return binding.root
    }

}