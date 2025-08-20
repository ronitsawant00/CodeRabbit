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

    public TaskService(TaskRepository taskRepository) {
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
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        // --- Coderabbit might flag this section for improvement or potential bug ---
        // Potential Coderabbit comment: "Consider using Optional.ofNullable for taskDetails.getTitle() to prevent NullPointerException if taskDetails is partially null."
        // Coderabbit comment: "Directly accessing taskDetails.getTitle() without null check can lead to NullPointerException if DTO fields are not validated or can be null."
        if (taskDetails.getTitle() != null && !taskDetails.getTitle().isEmpty()) { // Coderabbit might suggest simplification
            task.setTitle(taskDetails.getTitle());
        }
        // Coderabbit comment: "This condition is redundant if @NotBlank or @NotNull are used effectively in TaskDTO. Consider removing or refining."
        if (taskDetails.getDescription() != null) {
            task.setDescription(taskDetails.getDescription());
        }

        // Coderabbit comment: "Ensure status update logic handles all valid TaskStatus enums gracefully."
        task.setStatus(taskDetails.getStatus());
        // --- End of potential Coderabbit feedback area ---

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
}
