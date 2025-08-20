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

    /**
     * Constructs a TaskService2 using the given TaskRepository for data access.
     */
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Retrieve all Task entities.
     *
     * @return a list of all persisted Task objects; empty list if none exist
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Retrieve a Task by its identifier.
     *
     * @param id the primary key of the Task to retrieve
     * @return the Task with the given id
     * @throws ResourceNotFoundException if no Task exists with the provided id
     */
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    /**
     * Persists a new Task entity.
     *
     * Saves the given Task to the repository and returns the saved instance (may include generated fields such as an assigned id).
     *
     * @param task the Task to create
     * @return the saved Task instance
     */
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    /**
     * Update an existing Task's fields and persist the changes.
     *
     * Retrieves the Task with the given id and applies non-destructive updates:
     * - title is replaced only if `taskDetails.getTitle()` is non-null and non-empty,
     * - description is replaced only if `taskDetails.getDescription()` is non-null,
     * - status is always replaced with `taskDetails.getStatus()`.
     *
     * @param id the id of the Task to update
     * @param taskDetails a Task instance carrying new values for fields to update;
     *                    only the title and description are conditionally applied as described above
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
     * Delete the Task with the given id.
     *
     * Deletes the persisted Task identified by {@code id}. If no Task exists for the provided id,
     * a {@link ResourceNotFoundException} is thrown.
     *
     * @param id the id of the Task to delete
     * @throws ResourceNotFoundException if a Task with {@code id} does not exist
     */
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    /**
     * Retrieve all tasks with the given status.
     *
     * @param status the Task.TaskStatus to filter by
     * @return a list of matching Task entities; empty if no tasks match
     */
    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
}
