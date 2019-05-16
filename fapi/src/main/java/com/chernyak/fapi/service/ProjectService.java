package com.chernyak.fapi.service;

import com.chernyak.fapi.models.Project;

import java.util.List;

public interface ProjectService {
    Project getProjectById(Long id);
    Object getAllProjects(int page, int count, String sort);
    Project saveProject(Project project);
    Project updateProject(Project project);
    void deleteProject(Long id);
}