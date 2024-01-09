package com.dist.roundsimageloadertask.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dist.roundsimageloadertask.ImageData
import com.dist.roundsimageloadertask.R
import com.dist.roundsimageloadertask.base.Response
import com.dist.roundsimageloadertask.base.provideRetrofitClient
import com.dist.roundsimageloadertask.data.ImageDataRepository
import com.dist.roundsimageloadertask.data.api.ImageDetailsResponse
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    private val imageDataRepository: ImageDataRepository = ImageDataRepository(provideRetrofitClient())
    private val _viewState = MutableLiveData<MainViewState>()
    val observable: LiveData<MainViewState> = _viewState

    private var imageDataList: MutableList<ImageData> = ArrayList()

    init {
        fetchImageDetails()
    }

    private fun fetchImageDetails() {
        viewModelScope.launch {
            when (val response = imageDataRepository.getAll()) {
                is Response.Failure -> handleFailure()
                is Response.Success -> handleSuccess(response.value)
            }
        }
    }

    private fun handleSuccess(value: List<ImageDetailsResponse>) {
        imageDataList = value.toImageDetailsPresentationList().toMutableList()
        _viewState.value = MainViewState.ImageDetailsSuccess(imageDataList)
    }

    private fun handleFailure() {
        _viewState.value = MainViewState.Error.ServerError
    }

    fun invalidateImageCache() {
        _viewState.value = MainViewState.InvalidateImageCache(imageDataList)
    }

    sealed class MainViewState {
        object Loading : MainViewState()
        data class ImageDetailsSuccess(val imageDetails: List<ImageData>) : MainViewState()
        data class InvalidateImageCache(val imageDetails: List<ImageData>) : MainViewState()

        sealed class Error(@StringRes val messageRes: Int) : MainViewState() {
            object ServerError : Error(R.string.error_server)
        }
    }
}