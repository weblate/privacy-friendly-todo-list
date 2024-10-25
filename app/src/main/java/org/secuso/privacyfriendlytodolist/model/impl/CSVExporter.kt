package org.secuso.privacyfriendlytodolist.model.impl

import org.secuso.privacyfriendlytodolist.model.TodoList
import org.secuso.privacyfriendlytodolist.model.TodoSubtask
import org.secuso.privacyfriendlytodolist.model.TodoTask
import org.secuso.privacyfriendlytodolist.util.CSVBuilder
import java.io.Writer

class CSVExporter {
    private var hasAutoProgress = false
    private lateinit var csvBuilder: CSVBuilder

    private fun initialize(hasAutoProgress: Boolean, writer: Writer) {
        this.hasAutoProgress = hasAutoProgress
        csvBuilder = CSVBuilder(writer)
    }

    fun export(todoLists: List<TodoList>, todoTasks: List<TodoTask>,
               hasAutoProgress: Boolean, writer: Writer) {
        initialize(hasAutoProgress, writer)
        createHeading()
        exportTasks(todoLists, todoTasks)
        exportLists(todoLists)
    }

    private fun exportTasks(todoLists: List<TodoList>, todoTasks: List<TodoTask>) {
        for (todoTask in todoTasks) {
            var todoList: TodoList? = null
            val todoListId = todoTask.getListId()
            if (null != todoListId) {
                todoList = todoLists.find { currentTodoList ->
                    currentTodoList.getId() == todoListId
                }
            }
            if (todoTask.getSubtasks().isEmpty()) {
                createRow(todoList, todoTask)
            } else {
                for (todoSubtask in todoTask.getSubtasks()) {
                    createRow(todoList, todoTask, todoSubtask)
                }
            }
        }
    }

    private fun exportLists(todoLists: List<TodoList>) {
        for (todoList in todoLists) {
            if (todoList.getTasks().isEmpty()) {
                createRow(todoList)
            }
        }
    }

    private fun createHeading() {
        csvBuilder.addField("ListId")
        csvBuilder.addField("ListName")

        csvBuilder.addField("TaskId")
        csvBuilder.addField("TaskName")
        csvBuilder.addField("TaskCreationTime")
        csvBuilder.addField("TaskDoneTime")
        csvBuilder.addField("TaskDescription")
        csvBuilder.addField("TaskDeadline")
        csvBuilder.addField("TaskReminderTime")
        csvBuilder.addField("TaskRecurrencePattern")
        csvBuilder.addField("TaskProgress")
        csvBuilder.addField("TaskPriority")

        csvBuilder.addField("SubtaskId")
        csvBuilder.addField("SubtaskName")
        csvBuilder.addField("SubtaskDoneTime")

        csvBuilder.startNewRow()
    }

    private fun createRow(todoList: TodoList? = null, todoTask: TodoTask? = null,
                          todoSubtask: TodoSubtask? = null) {
        if (null != todoList) {
            csvBuilder.addField(todoList.getId())
            csvBuilder.addField(todoList.getName())
        } else {
            for (i in START_COLUMN_LIST..<START_COLUMN_TASK) {
                csvBuilder.addEmptyField()
            }
        }

        if (null != todoTask) {
            csvBuilder.addField(todoTask.getId())
            csvBuilder.addField(todoTask.getName())
            csvBuilder.addTimeField(todoTask.getCreationTime())
            csvBuilder.addTimeField(todoTask.getDoneTime())
            csvBuilder.addField(todoTask.getDescription())
            csvBuilder.addTimeField(todoTask.getDeadline())
            csvBuilder.addTimeField(todoTask.getReminderTime())
            csvBuilder.addField(todoTask.getRecurrencePattern().toString())
            csvBuilder.addField(todoTask.getProgress(hasAutoProgress))
            csvBuilder.addField(todoTask.getPriority().toString())
        } else {
            for (i in START_COLUMN_TASK..<START_COLUMN_SUBTASK) {
                csvBuilder.addEmptyField()
            }
        }

        if (null != todoSubtask) {
            csvBuilder.addField(todoSubtask.getId())
            csvBuilder.addField(todoSubtask.getName())
            csvBuilder.addTimeField(todoSubtask.getDoneTime())
        } else {
            for (i in START_COLUMN_SUBTASK..<COLUMN_COUNT) {
                csvBuilder.addEmptyField()
            }
        }

        csvBuilder.startNewRow()
    }

    companion object {
        /** Zero-based index of first column with to-do list data. */
        const val START_COLUMN_LIST = 0
        /** Zero-based index of first column with to-do task data. */
        const val START_COLUMN_TASK = 2
        /** Zero-based index of first column with to-do subtask data. */
        const val START_COLUMN_SUBTASK = 12
        const val COLUMN_COUNT = 15
    }
}