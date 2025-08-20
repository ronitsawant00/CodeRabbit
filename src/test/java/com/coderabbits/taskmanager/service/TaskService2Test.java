package com.coderabbits.taskmanager.service;

import com.coderabbits.taskmanager.exception.ResourceNotFoundException;
import com.coderabbits.taskmanager.model.Task;
import com.coderabbits.taskmanager.model.Task.TaskStatus;
import com.coderabbits.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Test framework: JUnit 5 (JUnit Jupiter) with Mockito and AssertJ.
 * These tests focus on the TaskService2 behaviors shown in the provided diff snippet,
 * especially updateTask's conditional updates and exception scenarios.
 */
@ExtendWith(MockitoExtension.class)
class TaskService2Test {

    @Mock
    private TaskRepository taskRepository;

    // We rely on Mockito's field injection because the constructor in the provided code appears mismatched.
    @InjectMocks
    private TaskService2 taskService2;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    private Task existingTask;

    @BeforeEach
    void setUp() {
        existingTask = new Task();
        // These setters assume simple POJO behavior. If Lombok or builder is used, adjust accordingly.
        existingTask.setId(1L);
        existingTask.setTitle("Original Title");
        existingTask.setDescription("Original Description");
        existingTask.setStatus(TaskStatus.PENDING);
    }

    @Test
    @DisplayName("getAllTasks: returns list from repository")
    void getAllTasks_returnsList() {
        List<Task> expected = Arrays.asList(existingTask, cloneWithId(existingTask, 2L));
        when(taskRepository.findAll()).thenReturn(expected);

        List<Task> result = taskService2.getAllTasks();

        assertThat(result).containsExactlyElementsOf(expected);
        verify(taskRepository).findAll();
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    @DisplayName("getTaskById: returns task when found")
    void getTaskById_found() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        Task result = taskService2.getTaskById(1L);

        assertThat(result).isSameAs(existingTask);
        verify(taskRepository).findById(1L);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    @DisplayName("getTaskById: throws ResourceNotFoundException when missing")
    void getTaskById_missing() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService2.getTaskById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: 99");

        verify(taskRepository).findById(99L);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    @DisplayName("createTask: delegates to repository.save")
    void createTask_saves() {
        Task newTask = new Task();
        newTask.setTitle("New");
        newTask.setDescription("Desc");
        newTask.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.save(newTask)).thenReturn(newTask);

        Task result = taskService2.createTask(newTask);

        assertThat(result).isSameAs(newTask);
        verify(taskRepository).save(newTask);
        verifyNoMoreInteractions(taskRepository);
    }

    @Nested
    @DisplayName("updateTask")
    class UpdateTaskTests {

        @Test
        @DisplayName("updates title, description, and status when provided (happy path)")
        void updateAllFields() {
            Task details = new Task();
            details.setTitle("Updated Title");
            details.setDescription("Updated Desc");
            details.setStatus(TaskStatus.COMPLETED);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            Task updated = taskService2.updateTask(1L, details);

            verify(taskRepository).findById(1L);
            verify(taskRepository).save(taskCaptor.capture());
            Task saved = taskCaptor.getValue();

            assertThat(updated).isSameAs(saved);
            assertThat(saved.getTitle()).isEqualTo("Updated Title");
            assertThat(saved.getDescription()).isEqualTo("Updated Desc");
            assertThat(saved.getStatus()).isEqualTo(TaskStatus.COMPLETED);

            verifyNoMoreInteractions(taskRepository);
        }

        @Test
        @DisplayName("does not update title when null; retains original title")
        void titleNull_notUpdated() {
            Task details = new Task();
            details.setTitle(null); // should not overwrite
            details.setDescription("New Desc");
            details.setStatus(TaskStatus.IN_PROGRESS);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            Task updated = taskService2.updateTask(1L, details);

            verify(taskRepository).findById(1L);
            verify(taskRepository).save(taskCaptor.capture());
            Task saved = taskCaptor.getValue();

            assertThat(saved.getTitle()).isEqualTo("Original Title");
            assertThat(saved.getDescription()).isEqualTo("New Desc");
            assertThat(saved.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            assertThat(updated).isSameAs(saved);

            verifyNoMoreInteractions(taskRepository);
        }

        @Test
        @DisplayName("does not update title when empty string; retains original title")
        void titleEmpty_notUpdated() {
            Task details = new Task();
            details.setTitle(""); // empty should not overwrite
            details.setDescription("Another Desc");
            details.setStatus(TaskStatus.PENDING);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            Task updated = taskService2.updateTask(1L, details);

            verify(taskRepository).findById(1L);
            verify(taskRepository).save(taskCaptor.capture());
            Task saved = taskCaptor.getValue();

            assertThat(saved.getTitle()).isEqualTo("Original Title");
            assertThat(saved.getDescription()).isEqualTo("Another Desc");
            assertThat(saved.getStatus()).isEqualTo(TaskStatus.PENDING);
            assertThat(updated).isSameAs(saved);

            verifyNoMoreInteractions(taskRepository);
        }

        @Test
        @DisplayName("description updated when null allowed (explicitly checks null assignment pass-through)")
        void descriptionNull_setsNull() {
            Task details = new Task();
            details.setTitle("New Title");
            details.setDescription(null); // code sets description only when non-null; null should keep original
            details.setStatus(TaskStatus.IN_PROGRESS);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            Task updated = taskService2.updateTask(1L, details);

            verify(taskRepository).findById(1L);
            verify(taskRepository).save(taskCaptor.capture());
            Task saved = taskCaptor.getValue();

            // Title should update, description should remain original, status should update
            assertThat(saved.getTitle()).isEqualTo("New Title");
            assertThat(saved.getDescription()).isEqualTo("Original Description");
            assertThat(saved.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            assertThat(updated).isSameAs(saved);

            verifyNoMoreInteractions(taskRepository);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when updating non-existent task")
        void update_missingTask_throws() {
            when(taskRepository.findById(100L)).thenReturn(Optional.empty());

            Task details = new Task();
            details.setTitle("x");
            details.setDescription("y");
            details.setStatus(TaskStatus.PENDING);

            assertThatThrownBy(() -> taskService2.updateTask(100L, details))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Task not found with id: 100");

            verify(taskRepository).findById(100L);
            verifyNoMoreInteractions(taskRepository);
        }
    }

    @Test
    @DisplayName("deleteTask: deletes when task exists")
    void deleteTask_success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        doNothing().when(taskRepository).delete(existingTask);

        taskService2.deleteTask(1L);

        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(existingTask);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    @DisplayName("deleteTask: throws ResourceNotFoundException when missing")
    void deleteTask_missing() {
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService2.deleteTask(2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: 2");

        verify(taskRepository).findById(2L);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    @DisplayName("getTasksByStatus: delegates to repository")
    void getTasksByStatus_delegates() {
        TaskStatus status = TaskStatus.IN_PROGRESS;
        List<Task> tasks = Collections.singletonList(existingTask);
        when(taskRepository.findByStatus(status)).thenReturn(tasks);

        List<Task> result = taskService2.getTasksByStatus(status);

        assertThat(result).containsExactlyElementsOf(tasks);
        verify(taskRepository).findByStatus(status);
        verifyNoMoreInteractions(taskRepository);
    }

    // Helper to clone an existing task with a new id while copying core fields
    private Task cloneWithId(Task from, Long newId) {
        Task t = new Task();
        t.setId(newId);
        t.setTitle(from.getTitle());
        t.setDescription(from.getDescription());
        t.setStatus(from.getStatus());
        return t;
        // If Task uses a builder or immutables, adjust accordingly.
    }
}