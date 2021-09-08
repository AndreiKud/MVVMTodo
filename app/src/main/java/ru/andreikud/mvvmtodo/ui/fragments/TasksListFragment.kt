package ru.andreikud.mvvmtodo.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.andreikud.mvvmtodo.R
import com.andreikud.mvvmtodo.databinding.FragmentTasksListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.andreikud.mvvmtodo.ui.adapters.TasksAdapter
import ru.andreikud.mvvmtodo.util.onQueryTextChanged
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.miSearch -> {
                true
            }
            R.id.miHideAllCompleted -> {
                item.isChecked = !item.isChecked
                true
            }
            R.id.miDeleteAllCompleted -> {
                true
            }
            R.id.miSortByDate -> {
                true
            }
            R.id.miSortByImportance -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}