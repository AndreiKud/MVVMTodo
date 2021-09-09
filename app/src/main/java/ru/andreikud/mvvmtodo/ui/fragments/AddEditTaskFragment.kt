package ru.andreikud.mvvmtodo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.andreikud.mvvmtodo.R
import com.andreikud.mvvmtodo.databinding.FragmentAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint
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
                cbImportance.isChecked = isImportant
                cbImportance.jumpDrawablesToCurrentState()
                tvCreatedDate.isVisible = task != null
                tvCreatedDate.text = "Created: ${task?.formattedTimeCreated}"
            }
        }
    }
}