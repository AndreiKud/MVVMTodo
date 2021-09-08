package ru.andreikud.mvvmtodo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.andreikud.mvvmtodo.R
import com.andreikud.mvvmtodo.databinding.FragmentTasksListBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.andreikud.mvvmtodo.ui.adapters.TasksAdapter
import ru.andreikud.mvvmtodo.viewmodel.TasksViewModel

@AndroidEntryPoint
class TasksListFragment : Fragment(R.layout.fragment_tasks_list) {

    private val viewModel: TasksViewModel by viewModels()
    private var binding: FragmentTasksListBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTasksListBinding.bind(view)

        val taskAdapter = TasksAdapter()
        binding?.apply {
            rvTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
        }
    }

}