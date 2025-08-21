package com.coderabbits.taskmanager.service;

import com.coderabbits.taskmanager.model.Task;
import com.coderabbits.taskmanager.repository.TaskRepository;
import com.coderabbits.taskmanager.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; 

@Service
public class TaskService2 {

    private final TaskRepository taskRepository;

    public TaskService2(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails) {
    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (taskDetails == null) {
            throw new IllegalArgumentException("taskDetails must not be null");
        }

        String title = taskDetails.getTitle();
        if (title != null && !title.trim().isEmpty()) {
            task.setTitle(title.trim());
        }

        if (taskDetails.getDescription() != null) {
            task.setDescription(taskDetails.getDescription());
        }

        if (taskDetails.getStatus() != null) {
            task.setStatus(taskDetails.getStatus());
        }

        return taskRepository.save(task);
    }    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
}
