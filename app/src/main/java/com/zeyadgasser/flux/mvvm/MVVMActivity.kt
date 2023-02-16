package com.zeyadgasser.flux.mvvm

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zeyadgasser.core.Effect
import com.zeyadgasser.core.Error
import com.zeyadgasser.core.InputStrategy.THROTTLE
import com.zeyadgasser.core.Progress
import com.zeyadgasser.core.State
import com.zeyadgasser.flux.databinding.ActivityMviBinding

class MVVMActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMviBinding
    private val viewModel: MVVMViewModel by viewModels { MVVMViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViews()
        lifecycleScope.launchWhenStarted {
            viewModel.observe().collect {
                when (it) {
                    is Error -> bindError(it)
                    is Effect -> bindEffect(it as MVVMEffect)
                    is State -> binding.bindState(it as MVVMState)
                    is Progress -> binding.toggleProgress(it)
                }
            }
        }
    }

    private fun bindViews() {
        binding = ActivityMviBinding.inflate(layoutInflater).apply {
            setContentView(root)
            changeBackgroundButton.setOnClickListener {
                viewModel.process(ChangeBackgroundInput(), THROTTLE)
            }
            showDialogButton.setOnClickListener { viewModel.process(ShowDialogInput) }
            showErrorButton.setOnClickListener { viewModel.process(ErrorInput) }
        }
    }

    private fun ActivityMviBinding.toggleProgress(progress: Progress) {
        if (progress.input is ChangeBackgroundInput) {
            progressBar.visibility = if (progress.isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun bindError(error: Error) = binding.apply { errorText.text = error.message }

    private fun bindEffect(effect: MVVMEffect) = when (effect) {
        is ShowDialogEffect -> AlertDialog.Builder(this).setTitle("Dialog")
            .setMessage("Dialog effect!").create().show()
    }

    private fun ActivityMviBinding.bindState(state: MVVMState) = when (state) {
        is ColorBackgroundState -> container.setBackgroundColor(state.color)
        InitialState -> Unit
    }
}