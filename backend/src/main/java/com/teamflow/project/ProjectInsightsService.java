package com.teamflow.project;

import com.teamflow.common.ApiException;
import com.teamflow.task.TaskRepository;
import com.teamflow.task.TaskService;
import com.teamflow.task.TaskStatus;
import com.teamflow.user.User;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectInsightsService {
 private final TaskRepository tasks;
 private final ProjectService projects;
 private final TaskService taskService;

 public ProjectInsightsService(TaskRepository tasks,ProjectService projects,TaskService taskService){this.tasks=tasks;this.projects=projects;this.taskService=taskService;}

 public record StatusMetric(String status,long total){}
 public record PriorityMetric(String priority,long total){}
 public record AssigneeMetric(Long userId,String name,long total,long completed){}
 public record DayMetric(LocalDate day,long created,long completed){}
 public record Overview(ProjectService.View project,long totalTasks,long completedTasks,double completionRate,long dueSoon,long overdue,List<StatusMetric> statuses,List<TaskService.View> recentTasks){}
 public record Analytics(LocalDate from,LocalDate to,long totalTasks,long completedTasks,double completionRate,List<DayMetric> taskTrend,List<StatusMetric> statuses,List<PriorityMetric> priorities,List<AssigneeMetric> topAssignees){}

 @Transactional(readOnly=true)
 public Overview overview(User actor,long projectId){
  var project=projects.get(actor,projectId);
  long total=tasks.countByProjectId(projectId),completed=tasks.countByProjectIdAndStatus(projectId,TaskStatus.DONE);
  LocalDate today=LocalDate.now(ZoneOffset.UTC);
  var recent=taskService.list(actor,projectId,null,null,null,null,0,5,"updatedAt","desc").getContent();
  return new Overview(project,total,completed,rate(total,completed),tasks.countByProjectIdAndDueDateBetweenAndStatusNot(projectId,today,today.plusDays(7),TaskStatus.DONE),tasks.countByProjectIdAndDueDateBeforeAndStatusNot(projectId,today,TaskStatus.DONE),statusMetrics(projectId),recent);
 }

 @Transactional(readOnly=true)
 public Analytics analytics(User actor,long projectId,LocalDate from,LocalDate to){
  projects.get(actor,projectId);
  LocalDate end=to==null?LocalDate.now(ZoneOffset.UTC):to;
  LocalDate start=from==null?end.minusDays(29):from;
  if(start.isAfter(end)||start.plusDays(365).isBefore(end))throw ApiException.badRequest("Choose a date range of up to 366 days.");
  Instant fromInstant=start.atStartOfDay(ZoneOffset.UTC).toInstant(),toExclusive=end.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
  Map<LocalDate,Long> created=new HashMap<>(),completedByDay=new HashMap<>();
  tasks.countCreatedByDay(projectId,fromInstant,toExclusive).forEach(row->created.put(row.getDay().toLocalDate(),row.getTotal()));
  tasks.countCompletedByDay(projectId,fromInstant,toExclusive).forEach(row->completedByDay.put(row.getDay().toLocalDate(),row.getTotal()));
  List<DayMetric> trend=new ArrayList<>();
  for(LocalDate day=start;!day.isAfter(end);day=day.plusDays(1))trend.add(new DayMetric(day,created.getOrDefault(day,0L),completedByDay.getOrDefault(day,0L)));
  long total=tasks.countByProjectId(projectId),done=tasks.countByProjectIdAndStatus(projectId,TaskStatus.DONE);
  var priorities=tasks.countByPriority(projectId).stream().map(v->new PriorityMetric(v.getPriority().name(),v.getTotal())).toList();
  var assignees=tasks.countByAssignee(projectId,PageRequest.of(0,5)).stream().map(v->new AssigneeMetric(v.getAssigneeId(),v.getAssigneeName(),v.getTotal(),v.getCompleted())).toList();
  return new Analytics(start,end,total,done,rate(total,done),trend,statusMetrics(projectId),priorities,assignees);
 }

 private List<StatusMetric> statusMetrics(long projectId){
  Map<TaskStatus,Long> counts=new EnumMap<>(TaskStatus.class);
  tasks.countByStatus(projectId).forEach(row->counts.put(row.getStatus(),row.getTotal()));
  return Arrays.stream(TaskStatus.values()).map(status->new StatusMetric(status.name(),counts.getOrDefault(status,0L))).toList();
 }
 private double rate(long total,long completed){return total==0?0:Math.round(completed*1000.0/total)/10.0;}
}
