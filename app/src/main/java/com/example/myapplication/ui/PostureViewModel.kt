package com.example.myapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.PostureState

class PostureViewModel : ViewModel() {
    private val _postureState = MutableLiveData<PostureState>()
    val postureState: LiveData<PostureState> = _postureState

    private val _angle = MutableLiveData<Int>()
    val angle: LiveData<Int> = _angle

    fun updatePosture(postureState: PostureState, angle: Int) {
        _postureState.value = postureState
        _angle.value = angle
    }
}