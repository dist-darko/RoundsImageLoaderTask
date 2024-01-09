package com.dist.roundsimageloadertask.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dist.roundsimageloadertask.ImageData
import com.dist.roundsimageloadertask.databinding.ActivityMainBinding
import com.dist.image_loader_lib.image_loader.ImageCacheImpl
import com.dist.image_loader_lib.image_loader.ImageLoaderImpl
import com.dist.image_loader_lib.image_loader.ImageLoaderProvider
import com.dist.roundsimageloadertask.presentation.list.ImageListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    private val activityScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var imageListAdapter: ImageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = MainViewModel()
        mainViewModel.observable.observe(this, Observer { state ->
            state ?: return@Observer

            renderState(state)
        })

        with(binding) {
            invalidateCacheButton.setOnClickListener {
                mainViewModel.invalidateImageCache()
            }
        }
    }

    private fun renderState(state: MainViewModel.MainViewState) {
        when (state) {
            is MainViewModel.MainViewState.Error.ServerError -> renderError(state.messageRes)
            is MainViewModel.MainViewState.ImageDetailsSuccess -> renderSuccessState(state.imageDetails)
            is MainViewModel.MainViewState.Loading -> renderLoading()
            is MainViewModel.MainViewState.InvalidateImageCache -> doInvalidateImageCache(state.imageDetails)
        }
    }

    private fun renderLoading() {
        //TODO implement some kind of loader
    }

    private fun renderError(messageRes: Int) {
        //TODO show toast/snack message
    }

    private fun renderSuccessState(imageDetails: List<ImageData>) {
        with(binding) {
            val imageLoader = ImageLoaderProvider.get(activityScope, this@MainActivity)
            imageList.layoutManager = LinearLayoutManager(this@MainActivity)
            imageListAdapter = ImageListAdapter(imageLoader)
            imageList.adapter = imageListAdapter
            imageListAdapter.addAll(imageDetails)
        }
    }

    private fun doInvalidateImageCache(imageDetails: List<ImageData>) {
        val imageCacheImpl = ImageCacheImpl(activityScope, this@MainActivity)
        imageCacheImpl.invalidateCache()
        if (imageDetails.isNotEmpty()) {
            imageListAdapter.removeAll()
            imageListAdapter.addAll(imageDetails)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }
}