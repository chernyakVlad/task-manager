package com.chernyak.fapi.controller;

import com.chernyak.fapi.models.Task;
import com.chernyak.fapi.service.TaskService;
import com.chernyak.fapi.validators.TaskValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    private TaskService taskService;
    private TaskValidator taskValidator;

    @Autowired
    public TaskController(TaskService taskService, TaskValidator taskValidator) {
        this.taskService = taskService;
        this.taskValidator = taskValidator;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id){
        return new ResponseEntity<>(taskService.getTaskById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/byUsername/{username}")
    public Object getAllTasksByAsignee(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "order") String order,
            @PathVariable String username){
        return taskService.getAllTasksByUsername(page, size, sort, order, username);
    }

    @GetMapping(value = "/byProject/{id}")
    public Object getAllTasksByProject(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "order") String order,
            @PathVariable Long id){
        return taskService.getAllTasksByProjectId(page, size, sort, order, id);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> saveTask(@RequestBody Task task, BindingResult bindingResult) {
        taskValidator.validate(task, bindingResult);
        if(bindingResult.hasErrors()){
            return  ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return new ResponseEntity<>(taskService.saveTask(task), HttpStatus.OK);
    }

    @PutMapping(value = "")
    public ResponseEntity<Task> updateTask(@RequestBody Task task) {
        return new ResponseEntity<>(taskService.updateTask(task), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?>  deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
