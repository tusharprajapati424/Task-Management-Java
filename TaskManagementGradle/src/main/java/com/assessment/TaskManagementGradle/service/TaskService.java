package com.assessment.TaskManagementGradle.service;

import com.assessment.TaskManagementGradle.entity.Task;
import com.assessment.TaskManagementGradle.entity.User;
import com.assessment.TaskManagementGradle.repository.TaskRepository;
import com.assessment.TaskManagementGradle.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;


    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id){
        return taskRepository.findById(id).orElse(null);
    }

//    public Task saveTask(Task task){
//        return taskRepository.save(task);
//    }

    public Task createTask(Task task, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("createTask 1 ::" + user.toString());
        task.setAssignedUser(user);// Set foreign key
        System.out.println("createTask 2::" + task.toString());
        return taskRepository.save(task);
    }

    public boolean deleteById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Task updateTask(Long id,Task updatedTask) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(updatedTask.getTitle());
                    task.setDescription(updatedTask.getDescription());
                    task.setStatus(updatedTask.getStatus());
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }
}
