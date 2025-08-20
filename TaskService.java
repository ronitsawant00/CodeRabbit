package com.coderabbits.taskmanager.service;

import com.coderabbits.taskmanager.model.Task;
import com.coderabbits.taskmanager.repository.TaskRepository;
import com.coderabbits.taskmanager.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; 

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Constructs a TaskService using the given TaskRepository.
     */
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Retrieves all Task entities.
     *
     * @return a list of all tasks persisted in the repository
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Retrieves a Task by its id.
     *
     * @param id the task's identifier
     * @return the Task with the given id
     * @throws ResourceNotFoundException if no Task is found for the provided id
     */
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    /**
     * Persists a new Task.
     *
     * Saves the provided Task entity and returns the persisted instance (including any generated identifiers or managed state).
     *
     * @param task the Task to create
     * @return the saved Task
     */
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    /**
     * Updates an existing Task with values from the provided Task object and persists the change.
     *
     * The method:
     * - Looks up the Task by `id` and throws ResourceNotFoundException if not found.
     * - If `taskDetails.getTitle()` is non-null and non-empty, replaces the existing title.
     * - If `taskDetails.getDescription()` is non-null, replaces the existing description.
     * - Unconditionally sets the status to `taskDetails.getStatus()`.
     *
     * @param id the identifier of the Task to update
     * @param taskDetails a Task object containing updated values; fields may be partially populated
     * @return the saved, updated Task
     * @throws ResourceNotFoundException if no Task exists with the given id
     */
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

    /**
     * Deletes the task with the specified id.
     *
     * Looks up the task by id and removes it from the repository.
     *
     * @param id the id of the task to delete
     * @throws ResourceNotFoundException if no task exists with the given id
     */
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    /**
     * Returns all tasks that have the given status.
     *
     * @param status the Task.TaskStatus to filter by
     * @return a list of Tasks matching the provided status; empty if none match
     */
    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
}
