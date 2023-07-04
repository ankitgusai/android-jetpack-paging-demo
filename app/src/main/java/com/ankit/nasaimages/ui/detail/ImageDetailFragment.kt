package com.ankit.nasaimages.ui.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ankit.nasaimages.R
import com.ankit.nasaimages.databinding.FragmentImageDetailBinding
import com.ankit.nasaimages.ui.MainViewModel
import com.ankit.nasaimages.ui.Response
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

/**
 * Displays selected image detail
 */
class ImageDetailFragment : Fragment() {
    private val sharedViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentImageDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentImageDetailBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.selectedItem.flatMapLatest { item ->
                    // Display the details we have from the list item
                    with(item!!) {
                        binding.tvTitleDetail.text = data[0].title
                        binding.tvDescriptionDetail.text = data[0].description
                        binding.tvDateDetails.text = data[0].date_created
                        setImage(links[0].href)
                    }

                    // Switch to the itemDetails Flow
                    sharedViewModel.itemDetails
                }.collect { highResUrlResponse ->
                    //THe bare minimum error handling.
                    when (highResUrlResponse) {
                        is Response.Error -> Toast.makeText(
                            requireContext(),
                            "Error : ${highResUrlResponse.exception.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()

                        is Response.Success -> setImage(highResUrlResponse.data)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // to avoid memory leaks.
    }

    private fun setImage(url: String) {
        Glide.with(binding.ivHighRes.context)
            .load(url)
            .placeholder(binding.ivHighRes.drawable) // the low res thumb image is the placeholder
            .error(R.drawable.error_image_24)
            .listener(object : RequestListener<Drawable?> { //An Extension for this listener can be created with lifecycle awareness for less boilerplate
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        binding.pbLoadingHighRes.visibility = View.GONE
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        binding.pbLoadingHighRes.visibility = View.GONE
                    }
                    return false
                }

            })
            .into(binding.ivHighRes)
    }
}