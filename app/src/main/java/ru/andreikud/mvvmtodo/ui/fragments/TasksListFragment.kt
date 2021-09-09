package ru.andreikud.mvvmtodo.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Checkable
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.andreikud.mvvmtodo.R
import com.andreikud.mvvmtodo.databinding.FragmentTasksListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.andreikud.mvvmtodo.ui.adapters.TasksAdapter
import ru.andreikud.mvvmtodo.util.onQueryTextChanged
import ru.andreikud.mvvmtodo.data.SortOrder
import ru.andreikud.mvvmtodo.data.model.Task
import ru.andreikud.mvvmtodo.viewmodel.TasksViewModel

@AndroidEntryPoint
class TasksListFragment : Fragment(R.layout.fragment_tasks_list), TasksAdapter.OnItemClickListener {

    private val viewModel: TasksViewModel by viewModels()
    private var binding: FragmentTasksListBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTasksListBinding.bind(view)

        val taskAdapter = TasksAdapter(this)
        
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

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_actions, menu)
        val searchView = menu.findItem(R.id.miSearch).actionView
        if (searchView is SearchView) {
            searchView.onQueryTextChanged { newText ->
                viewModel.queryNameFilter.value = newText
            }
        }

        val hideCompleted = menu.findItem(R.id.miHideAllCompleted)
        viewLifecycleOwner.lifecycleScope.launch {
            hideCompleted.isChecked = viewModel.preferences.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.miSearch -> {
                true
            }
            R.id.miHideAllCompleted -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedClicked(item.isChecked)
                true
            }
            R.id.miDeleteAllCompleted -> {
                true
            }
            R.id.miSortByDate -> {
                viewModel.onSortOrderClicked(SortOrder.BY_DATE)
                true
            }
            R.id.miSortByName -> {
                viewModel.onSortOrderClicked(SortOrder.BY_NAME)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(item: Task) {
        viewModel.onTaskSelected(item)
    }

    override fun onCheckBoxClick(item: Task, isChecked: Boolean) {
        viewModel.onCompletedStateChanged(item, isChecked)
    }
}