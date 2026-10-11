package com.teamflow.task;
import com.teamflow.common.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController
public class TaskController {
 private final TaskService service;private final CurrentUser current;
 public TaskController(TaskService s,CurrentUser c){service=s;current=c;}
 record Create(@NotBlank @Size(max=180) String title,@Size(max=5000) String description,TaskPriority priority,Long assigneeId,LocalDate dueDate){}
 record Update(@Size(min=1,max=180) String title,@Size(max=5000) String description,TaskPriority priority,Long assigneeId,LocalDate dueDate,@NotNull Long version){}
 record StatusChange(@NotNull TaskStatus status,@NotNull Long version){}
 @GetMapping("/api/v1/projects/{projectId}/tasks") Page<TaskService.View> list(Authentication a,@PathVariable long projectId,@RequestParam(required=false) TaskStatus status,@RequestParam(required=false) TaskPriority priority,@RequestParam(required=false) Long assigneeId,@RequestParam(required=false) String search,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,@RequestParam(defaultValue="updatedAt") String sort,@RequestParam(defaultValue="desc") String direction){return service.list(current.get(a),projectId,status,priority,assigneeId,search,page,size,sort,direction);}
 @PostMapping("/api/v1/projects/{projectId}/tasks") TaskService.View create(Authentication a,@PathVariable long projectId,@Valid @RequestBody Create r){return service.create(current.get(a),projectId,r.title(),r.description(),r.priority()==null?TaskPriority.MEDIUM:r.priority(),r.assigneeId(),r.dueDate());}
 @GetMapping("/api/v1/tasks/{id}") TaskService.View get(Authentication a,@PathVariable long id){return service.get(current.get(a),id);}
 @PatchMapping("/api/v1/tasks/{id}") TaskService.View update(Authentication a,@PathVariable long id,@Valid @RequestBody Update r){return service.update(current.get(a),id,r.title(),r.description(),r.priority(),r.assigneeId(),r.dueDate(),r.version());}
 @PatchMapping("/api/v1/tasks/{id}/status") TaskService.View status(Authentication a,@PathVariable long id,@Valid @RequestBody StatusChange r){return service.status(current.get(a),id,r.status(),r.version());}
 @GetMapping("/api/v1/tasks/{id}/history") java.util.List<TaskService.AuditView> history(Authentication a,@PathVariable long id){return service.history(current.get(a),id);}
}
