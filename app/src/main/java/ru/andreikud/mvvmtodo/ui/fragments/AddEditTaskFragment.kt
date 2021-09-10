package ru.andreikud.mvvmtodo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.andreikud.mvvmtodo.R
import com.andreikud.mvvmtodo.databinding.FragmentAddEditTaskBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ru.andreikud.mvvmtodo.ui.viewmodels.AddEditTaskEvent
import ru.andreikud.mvvmtodo.ui.viewmodels.AddEditTaskViewModel

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()

    private var binding: FragmentAddEditTaskBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddEditTaskBinding.bind(view)

        binding?.apply {
            with(viewModel) {
                etTaskName.setText(taskName)
                cbImportance.isChecked = isTaskImportant
                cbImportance.jumpDrawablesToCurrentState()
                tvCreatedDate.isVisible = task != null
                tvCreatedDate.text = "Created: ${task?.formattedTimeCreated}"
            }

            etTaskName.addTextChangedListener { editable ->
                viewModel.taskName = editable.toString()
            }
            cbImportance.setOnCheckedChangeListener { _, isChecked ->
                viewModel.isTaskImportant = isChecked
            }

            fabAddTask.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is AddEditTaskEvent.InvalidName -> {
                        Snackbar.make(
                            requireView(),
                            "Invalid name: ${event.msg}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is AddEditTaskEvent.Updated -> {
                        binding?.etTaskName?.clearFocus()
                        setFragmentResult(
                            "add_edit_task",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditTaskEvent.Created -> {
                        binding?.etTaskName?.clearFocus()
                        setFragmentResult(
                            "add_edit_task",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
}