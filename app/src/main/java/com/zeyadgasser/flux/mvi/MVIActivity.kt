package com.zeyadgasser.flux.mvi

import android.graphics.Color
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Error
import com.zeyadgasser.core.InputStrategy.THROTTLE
import com.zeyadgasser.core.Progress
import com.zeyadgasser.core.State
import com.zeyadgasser.flux.databinding.ActivityMviBinding
import kotlinx.coroutines.launch

class MVIActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMviBinding
    private val viewModel: MVIViewModel by viewModels { MVIViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViews()
        lifecycleScope.launchWhenCreated { viewModel.bind(InitialState) }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observe().collect {
                    when (it) {
                        is Effect -> bindEffect(it as MVIEffect)
                        is State -> binding.bindState(it as MVIState)
                        is Error -> binding.bindError(it)
                        is Progress -> binding.toggleProgress(it)
                    }
                }
            }
        }
    }

    private fun bindViews() {
        binding = ActivityMviBinding.inflate(layoutInflater).apply {
            setContentView(root)
            changeBackgroundButton
                .setOnClickListener { viewModel.process(ChangeBackgroundInput, THROTTLE) }
            showDialogButton.setOnClickListener { viewModel.process(ShowDialogInput) }
            showErrorButton.setOnClickListener { viewModel.process(ErrorInput) }
        }
    }

    private fun ActivityMviBinding.toggleProgress(progress: Progress) {
        if (progress.input is ChangeBackgroundInput) {
            progressBar.visibility = if (progress.isLoading) VISIBLE else GONE
        }
    }

    private fun ActivityMviBinding.bindError(error: Error) {
        errorText.text = error.message
    }

    private fun bindEffect(effect: MVIEffect) = when (effect) {
        is ShowDialogEffect -> AlertDialog.Builder(this)
            .setTitle("Dialog")
            .setMessage("Dialog effect!")
            .create().show()
    }

    private fun ActivityMviBinding.bindState(state: MVIState) = when (state) {
        is RedBackgroundState -> container.setBackgroundColor(Color.RED)
        InitialState -> Unit
    }
}