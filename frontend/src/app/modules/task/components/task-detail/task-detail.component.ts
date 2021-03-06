import { Component, OnInit, Output, ViewChild } from '@angular/core';
import { Task } from '../../models/Task';
import { Comment } from '../../models/Comment';
import { ActivatedRoute } from '@angular/router';
import { TaskService } from 'src/app/services/task.service';
import { AuthService } from 'src/app/services/authentication.service';
import { CommentService } from 'src/app/services/comment.service';
import { ProjectService } from 'src/app/services/project.service';
import { Project } from 'src/app/modules/project/models/Project';
import { BsModalService, BsModalRef } from 'ngx-bootstrap';
import { AddFileModalComponent } from 'src/app/modules/file/component/add-file-modal/add-file-modal.component';
import { projectionDef } from '@angular/core/src/render3';
import { NewTaskModalComponent } from '../new-task-modal/new-task-modal.component';
import { FileService } from 'src/app/services/file.service';
import { FileListComponent } from 'src/app/modules/file/component/file-list/file-list.component';
import { AssigneeModalComponent } from '../assignee-modal/assignee-modal.component';
import { CommentsListComponent } from 'src/app/modules/comment/comments-list/comments-list.component';

@Component({
  selector: 'app-task-detail',
  templateUrl: './task-detail.component.html',
  styleUrls: ['./task-detail.component.less']
})
export class TaskDetailComponent implements OnInit {

  @ViewChild(FileListComponent)
  private fileListComponent: FileListComponent;
  @ViewChild(CommentsListComponent)
  private commentsListComponent: CommentsListComponent;
  task: Task;
  project: Project;
  public modalRef: BsModalRef;  
  role: string; 
  text: string;
  newComment: Comment;

  ready = false;
  itMyTask = false;


  constructor(private activateRoute: ActivatedRoute,
    private commentService: CommentService,
    private projectService: ProjectService,
    private taskService: TaskService,
    private authService: AuthService,
    private modalService: BsModalService,
    private fileService: FileService) {
    
  } 

  ngOnInit() {    
    this.updateTask();     
    this.role = this.authService.currentUsersRole;    
  }

  updateTask() {
    const id = this.activateRoute.snapshot.params['id'];
    this.taskService.getById(id).subscribe(data => {  
      this.task = data;
      this.itMyTask = this.authService.currentUsername == this.task.assignee ? true : false;
      this.projectService.getById(this.task.projectId).subscribe(data => {
        this.project = data;
        this.ready = true;  
      })            
    },
    error => {
      console.log('error');
    });
  }

  _editTask(editableTask: Task){
    const initialState = {     
      projectId: this.project.id,
      task: editableTask,
      editMode: true
    };
    this.modalRef = this.modalService.show(NewTaskModalComponent, {initialState});
    this.modalRef.content.updateTask.subscribe(() => {
      this.taskService.updateTask(this.task).subscribe( data => {         
      });
    })
  }

  onInProgressClick(){
    this.task.taskStatus = "IN_PROGRESS";
    this.taskService.updateTask(this.task).subscribe(() => {     
    });
  }

  onReadyForTestClick(){
    const initialState = {     
      role: 'TESTER',
      projectId: this.task.projectId
    };
    this.modalRef = this.modalService.show(AssigneeModalComponent, {initialState});
    this.modalRef.content.assigneeSelected.subscribe(data=>{
      this.task.taskStatus = "READY_FOR_TEST";
      this.task.assignee = data;
      this.itMyTask = this.authService.currentUsername == this.task.assignee ? true : false;
      this.taskService.updateTask(this.task).subscribe( data => {         
      });
    }) 
  }

  onReopenClick(){
    const initialState = {     
      role: 'DEVELOPER',
      projectId: this.task.projectId
    };
    this.modalRef = this.modalService.show(AssigneeModalComponent, {initialState});
    this.modalRef.content.assigneeSelected.subscribe(data=>{
      this.task.taskStatus = "REOPEN";
      this.task.assignee = data;
      this.itMyTask = this.authService.currentUsername == this.task.assignee ? true : false;
      this.taskService.updateTask(this.task).subscribe( data => {         
      });
    }) 
  }

  onClosedClick(){
    this.task.taskStatus = "CLOSED";
    this.taskService.updateTask(this.task).subscribe(() => {     
    });
  }

  _submitClick(){
    this.newComment = new Comment(); 
    this.newComment.author = this.authService.currentUsername;
    this.newComment.description = this.text;
    this.newComment.taskId = this.task.id;
    this.text = '';
    this.commentService.saveComment(this.newComment).subscribe(()=>{
      this.commentsListComponent.updateComments();
    })
    
  }

  _openFileModal() { 
    const initialState = {     
      taskId: this.task.id,
      projectId: this.task.projectId
    };
    this.modalRef = this.modalService.show(AddFileModalComponent, {initialState});
    this.modalRef.content.uploadFiles.subscribe(data => {
      for(const file of data){
        this.fileService.saveFile(file, this.task.id, this.task.projectId).subscribe(()=>{
          this.fileListComponent._updateFileList(); 
        }) 
      }      
    })     
  }

  isPm() {
    if(this.authService.currentUsersRole === 'PROJECT_MANAGER'
      && this.authService.currentUsername === this.project.manager){
      return true;
    }
    return false;
  }
}
