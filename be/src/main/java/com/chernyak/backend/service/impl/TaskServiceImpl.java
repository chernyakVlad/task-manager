package com.chernyak.backend.service.impl;

import com.chernyak.backend.entity.Project;
import com.chernyak.backend.entity.Task;
import com.chernyak.backend.entity.User;
import com.chernyak.backend.repository.ProjectRepository;
import com.chernyak.backend.repository.TaskRepository;
import com.chernyak.backend.repository.UserRepository;
import com.chernyak.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private ProjectRepository projectRepository;

    @Autowired
    public  TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public Page<Task> getTaskByAsigneeUsername(int page, int count, String sort, String order, String username) {
        Sort.Direction direction = Sort.Direction.fromString(order);
        Pageable pageRequest = PageRequest.of(page, count, Sort.by(direction, sort));
        Optional<User> user = userRepository.findByUsername(username);
        return taskRepository.findAllByAssigneeId(pageRequest, user.get());
    }

    @Override
    public Page<Task> getTasksByProjectId(int page, int count, String sort, String order, Long id) {
        Sort.Direction direction = Sort.Direction.fromString(order);
        Pageable pageRequest = PageRequest.of(page, count, Sort.by(direction, sort));
        return taskRepository.findAllByProjectIdId(pageRequest, id);
    }

    @Override
    public Page<Task> findAll(int page, int count, String sort, String order, Specification<Task> spec) {
        Sort.Direction direction = Sort.Direction.fromString(order);
        Pageable pageRequest = PageRequest.of(page, count, Sort.by(direction, sort));
        return taskRepository.findAll(spec, pageRequest);
    }

    @Override
    public Task saveTask(Task task) {
        if(task.getTicketCode() == null) {
            Optional<Project> project = projectRepository.findById(task.getProjectId().getId());
            if(!project.isPresent()){
                throw new RuntimeException("Project not found");
            }
            task.setTicketCode(project.get().getCode() + "-" + (taskRepository.countByProjectId(project.get()) + 1));
        }
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
