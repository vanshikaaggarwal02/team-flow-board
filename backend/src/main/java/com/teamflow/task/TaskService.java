package com.teamflow.task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamflow.audit.*;
import com.teamflow.common.*;
import com.teamflow.project.*;
import com.teamflow.user.*;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class TaskService {
 private final TaskRepository tasks;private final ProjectRepository projects;private final ProjectService projectService;private final ProjectMemberRepository members;private final UserRepository users;private final AuditRepository audits;private final ObjectMapper json;
 public TaskService(TaskRepository t,ProjectRepository p,ProjectService ps,ProjectMemberRepository m,UserRepository u,AuditRepository a,ObjectMapper j){tasks=t;projects=p;projectService=ps;members=m;users=u;audits=a;json=j;}
 public record View(Long id,Long projectId,String title,String description,TaskStatus status,TaskPriority priority,Long assigneeId,String assigneeName,Long creatorId,LocalDate dueDate,long version,Instant createdAt,Instant updatedAt){}
 public record AuditView(Long id,String action,String oldValue,String newValue,String actorName,Instant createdAt){}
 @Transactional(readOnly=true) public Page<View> list(User actor,long project,TaskStatus status,TaskPriority priority,Long assignee,String search,int page,int size,String sort,String direction){projectService.membership(project,actor.id);if(page<0||size<1||size>100)throw ApiException.badRequest("Page must be nonnegative and size must be between 1 and 100.");if(!Set.of("createdAt","updatedAt","dueDate","priority").contains(sort))throw ApiException.badRequest("Unsupported sort field.");Sort.Direction dir;try{dir=Sort.Direction.fromString(direction);}catch(Exception e){throw ApiException.badRequest("Unsupported sort direction.");}Pageable pageable=PageRequest.of(page,size,Sort.by(dir,sort));Specification<Task> spec=(root,q,cb)->cb.equal(root.get("project").get("id"),project);if(status!=null)spec=spec.and((r,q,cb)->cb.equal(r.get("status"),status));if(priority!=null)spec=spec.and((r,q,cb)->cb.equal(r.get("priority"),priority));if(assignee!=null)spec=spec.and((r,q,cb)->cb.equal(r.get("assignee").get("id"),assignee));if(search!=null&&!search.isBlank()){String term="%"+search.trim().toLowerCase()+"%";spec=spec.and((r,q,cb)->cb.or(cb.like(cb.lower(r.get("title")),term),cb.like(cb.lower(r.get("description")),term)));}return tasks.findAll(spec,pageable).map(this::view);}
 @Transactional public View create(User actor,long project,String title,String description,TaskPriority priority,Long assignee,LocalDate due){var p=projects.findById(project).orElseThrow(ApiException::notFound);projectService.membership(project,actor.id);Task t=new Task(p,title.trim(),description,priority,actor);if(assignee!=null)t.assignee=validAssignee(project,assignee);t.dueDate=due;t=tasks.save(t);audit(t,actor,"TASK_CREATED",null,Map.of("title",t.title,"status",t.status.name(),"priority",t.priority.name()));tasks.flush();return view(t);}
 @Transactional public View update(User actor,long id,String title,String description,TaskPriority priority,Long assignee,LocalDate due,Long version){Task t=accessible(actor,id);if(version!=t.version)throw ApiException.conflict("This task has changed. Refresh before editing.");Map<String,Object> before=fields(t);if(title!=null)t.title=title.trim();t.description=description;if(priority!=null)t.priority=priority;t.assignee=assignee==null||assignee==0?null:validAssignee(t.project.getId(),assignee);t.dueDate=due;var after=fields(t);if(!before.equals(after))audit(t,actor,"TASK_UPDATED",before,after);tasks.flush();return view(t);}
 @Transactional public View status(User actor,long id,TaskStatus target,Long version){Task t=accessible(actor,id);if(version!=t.version)throw ApiException.conflict("This task has changed. Refresh before editing.");if(!t.status.canMoveTo(target))throw ApiException.badRequest("That status transition is not allowed.");String old=t.status.name();t.status=target;audit(t,actor,"STATUS_CHANGED",Map.of("status",old),Map.of("status",target.name()));tasks.flush();return view(t);}
 @Transactional(readOnly=true) public View get(User actor,long id){return view(accessible(actor,id));}
 @Transactional(readOnly=true) public List<AuditView> history(User actor,long id){Task t=accessible(actor,id);return audits.findByTaskIdOrderByCreatedAtDesc(t.id).stream().map(a->new AuditView(a.id,a.action,a.oldValue,a.newValue,a.actor.getName(),a.createdAt)).toList();}
 private Task accessible(User actor,long id){Task t=tasks.findById(id).orElseThrow(ApiException::notFound);projectService.membership(t.project.getId(),actor.id);return t;}
 private User validAssignee(long project,long id){if(!members.existsById(new ProjectMemberKey(project,id)))throw ApiException.badRequest("Assignee must be a member of this project.");return users.findById(id).orElseThrow(ApiException::notFound);}
 private Map<String,Object> fields(Task t){var m=new LinkedHashMap<String,Object>();m.put("title",t.title);m.put("description",t.description);m.put("priority",t.priority.name());m.put("assigneeId",t.assignee==null?null:t.assignee.getId());m.put("dueDate",t.dueDate==null?null:t.dueDate.toString());return m;}
 private void audit(Task t,User actor,String action,Object old,Object next){try{audits.save(new AuditLog(t,actor,action,old==null?null:json.writeValueAsString(old),next==null?null:json.writeValueAsString(next)));}catch(Exception e){throw new IllegalStateException("Could not record task history",e);}}
 private View view(Task t){return new View(t.id,t.project.getId(),t.title,t.description,t.status,t.priority,t.assignee==null?null:t.assignee.getId(),t.assignee==null?null:t.assignee.getName(),t.creator.getId(),t.dueDate,t.version,t.createdAt,t.updatedAt);}
}
