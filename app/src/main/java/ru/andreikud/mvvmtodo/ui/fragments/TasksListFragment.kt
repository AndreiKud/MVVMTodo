package ru.andreikud.mvvmtodo.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andreikud.mvvmtodo.R
import com.andreikud.mvvmtodo.databinding.FragmentTasksListBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.andreikud.mvvmtodo.ui.adapters.TasksAdapter
import ru.andreikud.mvvmtodo.util.onQueryTextChanged
import ru.andreikud.mvvmtodo.data.SortOrder
import ru.andreikud.mvvmtodo.data.model.Task
import ru.andreikud.mvvmtodo.ui.viewmodels.TaskEvent
import ru.andreikud.mvvmtodo.ui.viewmodels.TasksViewModel

@AndroidEntryPoint
class TasksListFragment : Fragment(R.layout.fragment_tasks_list), TasksAdapter.OnItemClickListener {

    private val viewModel: TasksViewModel by viewModels()
    private var binding: FragmentTasksListBinding? = null
    private lateinit var searchView: SearchView
    private lateinit var searchItem: MenuItem

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

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val item = taskAdapter.currentList[viewHolder.adapterPosition]
                    item?.let(viewModel::onTaskSwiped)
                }
            }).attachToRecyclerView(rvTasks)

            fabAddTask.setOnClickListener {
                viewModel.onTaskAddClick()
            }
        }

        setFragmentResultListener(
            "add_edit_task"
        ) { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is TaskEvent.TaskDeleted -> {
                        Snackbar.make(requireView(), "Task was deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.undoDeletion(event.task)
                            }.show()
                    }
                    is TaskEvent.NavigateToAddTaskScreen -> {
                        val action =
                            TasksListFragmentDirections.actionTasksListFragmentToAddEditTaskFragment(
                                "New Task"
                            )
                        findNavController().navigate(action)
                    }
                    is TaskEvent.NavigateToEditTaskScreen -> {
                        val action =
                            TasksListFragmentDirections.actionTasksListFragmentToAddEditTaskFragment(
                                "Edit Task",
                                event.task
                            )
                        findNavController().navigate(action)
                    }
                    is TaskEvent.ShowConfirmationMethod -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    is TaskEvent.ShowDeleteAllCompletedConfirmation -> {
                        val action =
                            DeleteAllCompletedDialogFragmentDirections.actionGlobalDeleteAllCompletedDialogFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_actions, menu)
        searchItem = menu.findItem(R.id.miSearch)
        searchView = searchItem.actionView as SearchView
        val pendingQuery = viewModel.queryNameFilter.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged { newText ->
            viewModel.queryNameFilter.value = newText
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
                viewModel.onHideCompletedClick(item.isChecked)
                true
            }
            R.id.miDeleteAllCompleted -> {
                viewModel.onDeleteAllCompletedClick()
                true
            }
            R.id.miSortByDate -> {
                viewModel.onSortOrderClick(SortOrder.BY_DATE)
                true
            }
            R.id.miSortByName -> {
                viewModel.onSortOrderClick(SortOrder.BY_NAME)
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

    override fun onDestroyView() {
        searchView.setOnQueryTextListener(null)
        super.onDestroyView()
    }
}