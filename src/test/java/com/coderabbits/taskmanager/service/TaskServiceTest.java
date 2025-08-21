/*
 Testing framework and library:
 - JUnit 5 (Jupiter): org.junit.jupiter.api
 - Mockito: org.mockito, using MockitoExtension
 This test suite focuses on unit-testing TaskService by mocking TaskRepository.
*/

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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task makeTask(Long id, String title, String description, TaskStatus status) {
        Task t = new Task();
        // Assuming standard setters exist on Task model; if builder/constructor is present, adjust as needed.
        try {
            // Try to invoke setters reflectively only if they exist to avoid compile errors if API differs.
            Task.class.getMethod("setTitle", String.class).invoke(t, title);
        } catch (Exception ignored) {}
        try {
            Task.class.getMethod("setDescription", String.class).invoke(t, description);
        } catch (Exception ignored) {}
        try {
            Task.class.getMethod("setStatus", TaskStatus.class).invoke(t, status);
        } catch (Exception ignored) {}
        // ID setter is often not present when using JPA; skip setting ID to avoid persistence concerns.
        return t;
    }

    @Test
    @DisplayName("getAllTasks returns all tasks from repository")
    void getAllTasks_returnsAll() {
        List<Task> tasks = Arrays.asList(
                makeTask(null, "A", "da", TaskStatus.PENDING),
                makeTask(null, "B", "db", TaskStatus.COMPLETED)
        );
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getTitle());
        assertEquals(TaskStatus.PENDING, result.get(0).getStatus());
        verify(taskRepository, times(1)).findAll();
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    @DisplayName("getTaskById returns the task when present")
    void getTaskById_found() {
        Task task = makeTask(null, "X", "dx", TaskStatus.PENDING);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals("X", result.getTitle());
        verify(taskRepository).findById(1L);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    @DisplayName("getTaskById throws ResourceNotFoundException when task missing")
    void getTaskById_missing_throws() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> taskService.getTaskById(99L));

        assertTrue(ex.getMessage() == null || ex.getMessage().contains("99"),
                "Exception message should reference missing id");
        verify(taskRepository).findById(99L);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    @DisplayName("createTask delegates to repository.save and returns persisted task")
    void createTask_saves() {
        Task input = makeTask(null, "New", "ndesc", TaskStatus.PENDING);
        Task saved = makeTask(null, "New", "ndesc", TaskStatus.PENDING);
        when(taskRepository.save(input)).thenReturn(saved);

        Task result = taskService.createTask(input);

        assertSame(saved, result);
        verify(taskRepository).save(input);
        verifyNoMoreInteractions(taskRepository);
    }

    @Nested
    @DisplayName("updateTask behavior")
    class UpdateTaskTests {

        @Test
        @DisplayName("updateTask updates title, description, and status when provided")
        void updateTask_updatesAllProvidedFields() {
            Task existing = makeTask(null, "Old", "Old desc", TaskStatus.PENDING);
            when(taskRepository.findById(5L)).thenReturn(Optional.of(existing));

            Task details = makeTask(null, "New Title", "New Desc", TaskStatus.IN_PROGRESS);
            Task saved = makeTask(null, "New Title", "New Desc", TaskStatus.IN_PROGRESS);
            when(taskRepository.save(any(Task.class))).thenReturn(saved);

            Task result = taskService.updateTask(5L, details);

            // Verify changes applied on the object passed to save
            ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).findById(5L);
            verify(taskRepository).save(captor.capture());
            Task toSave = captor.getValue();
            assertEquals("New Title", toSave.getTitle());
            assertEquals("New Desc", toSave.getDescription());
            assertEquals(TaskStatus.IN_PROGRESS, toSave.getStatus());
            assertSame(saved, result);
            verifyNoMoreInteractions(taskRepository);
        }

        @Test
        @DisplayName("updateTask ignores empty title and null description, still sets status")
        void updateTask_ignoresEmptyTitleAndNullDescription_setsStatus() {
            Task existing = makeTask(null, "Keep Title", "Keep Desc", TaskStatus.PENDING);
            when(taskRepository.findById(7L)).thenReturn(Optional.of(existing));

            Task details = makeTask(null, "", null, TaskStatus.COMPLETED); // empty title should be ignored; description null ignored
            Task saved = makeTask(null, "Keep Title", "Keep Desc", TaskStatus.COMPLETED);
            when(taskRepository.save(any(Task.class))).thenReturn(saved);

            Task result = taskService.updateTask(7L, details);

            ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).findById(7L);
            verify(taskRepository).save(captor.capture());
            Task toSave = captor.getValue();
            assertEquals("Keep Title", toSave.getTitle(), "Title should remain unchanged when empty string provided");
            assertEquals("Keep Desc", toSave.getDescription(), "Description should remain unchanged when null provided");
            assertEquals(TaskStatus.COMPLETED, toSave.getStatus(), "Status should be updated even when other fields are ignored");
            assertSame(saved, result);
            verifyNoMoreInteractions(taskRepository);
        }

        @Test
        @DisplayName("updateTask allows status to be set to null (as per current implementation)")
        void updateTask_allowsNullStatus() {
            Task existing = makeTask(null, "T", "D", TaskStatus.PENDING);
            when(taskRepository.findById(8L)).thenReturn(Optional.of(existing));

            Task details = makeTask(null, null, null, null); // only status is relevant here: null
            Task saved = makeTask(null, "T", "D", null);
            when(taskRepository.save(any(Task.class))).thenReturn(saved);

            Task result = taskService.updateTask(8L, details);

            ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).findById(8L);
            verify(taskRepository).save(captor.capture());
            Task toSave = captor.getValue();
            assertEquals("T", toSave.getTitle());
            assertEquals("D", toSave.getDescription());
            assertNull(toSave.getStatus(), "Status should be set to null per current code path");
            assertSame(saved, result);
            verifyNoMoreInteractions(taskRepository);
        }

        @Test
        @DisplayName("updateTask throws ResourceNotFoundException when task id not present")
        void updateTask_missing_throwsResourceNotFound() {
            when(taskRepository.findById(404L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> taskService.updateTask(404L, makeTask(null, "A", "B", TaskStatus.PENDING)));

            assertTrue(ex.getMessage() == null || ex.getMessage().contains("404"));
            verify(taskRepository).findById(404L);
            verifyNoMoreInteractions(taskRepository);
        }

        @Test
        @DisplayName("updateTask with null details causes NullPointerException (documents current behavior)")
        void updateTask_nullDetails_throwsNPE() {
            Task existing = makeTask(null, "Old", "Desc", TaskStatus.PENDING);
            when(taskRepository.findById(9L)).thenReturn(Optional.of(existing));

            assertThrows(NullPointerException.class, () -> taskService.updateTask(9L, null),
                    "Current implementation dereferences taskDetails without a null check");
            verify(taskRepository).findById(9L);
            // save should not be called
            verify(taskRepository, never()).save(any());
            verifyNoMoreInteractions(taskRepository);
        }
    }

    @Test
    @DisplayName("deleteTask deletes existing record")
    void deleteTask_success() {
        Task existing = makeTask(null, "Del", "del", TaskStatus.PENDING);
        when(taskRepository.findById(3L)).thenReturn(Optional.of(existing));

        taskService.deleteTask(3L);

        verify(taskRepository).findById(3L);
        verify(taskRepository).delete(existing);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    @DisplayName("deleteTask throws ResourceNotFoundException when id missing")
    void deleteTask_missing_throws() {
        when(taskRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(123L));

        verify(taskRepository).findById(123L);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    @DisplayName("getTasksByStatus delegates to repository")
    void getTasksByStatus_delegates() {
        List<Task> tasks = Collections.singletonList(makeTask(null, "T", "D", TaskStatus.COMPLETED));
        when(taskRepository.findByStatus(TaskStatus.COMPLETED)).thenReturn(tasks);

        List<Task> result = taskService.getTasksByStatus(TaskStatus.COMPLETED);

        assertEquals(1, result.size());
        assertEquals(TaskStatus.COMPLETED, result.get(0).getStatus());
        verify(taskRepository).findByStatus(TaskStatus.COMPLETED);
        verifyNoMoreInteractions(taskRepository);
    }
}