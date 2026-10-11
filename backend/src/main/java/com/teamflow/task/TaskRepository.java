package com.teamflow.task;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
public interface TaskRepository extends JpaRepository<Task,Long>, JpaSpecificationExecutor<Task> {
 Page<Task> findByProjectId(Long projectId,Pageable pageable);
 interface StatusCount { TaskStatus getStatus(); Long getTotal(); }
 interface PriorityCount { TaskPriority getPriority(); Long getTotal(); }
 interface AssigneeCount { Long getAssigneeId(); String getAssigneeName(); Long getTotal(); Long getCompleted(); }
 interface DayCount { java.sql.Date getDay(); Long getTotal(); }
 @Query("select t.status as status, count(t) as total from Task t where t.project.id=:projectId group by t.status")
 List<StatusCount> countByStatus(@Param("projectId") long projectId);
 @Query("select t.priority as priority, count(t) as total from Task t where t.project.id=:projectId group by t.priority")
 List<PriorityCount> countByPriority(@Param("projectId") long projectId);
 @Query("select t.assignee.id as assigneeId, t.assignee.name as assigneeName, count(t) as total, sum(case when t.status=com.teamflow.task.TaskStatus.DONE then 1 else 0 end) as completed from Task t where t.project.id=:projectId and t.assignee is not null group by t.assignee.id,t.assignee.name order by count(t) desc")
 List<AssigneeCount> countByAssignee(@Param("projectId") long projectId, Pageable pageable);
 @Query(value="select cast(created_at at time zone 'UTC' as date) as day, count(*) as total from tasks where project_id=:projectId and created_at>=:from and created_at<:to group by cast(created_at at time zone 'UTC' as date) order by day",nativeQuery=true)
 List<DayCount> countCreatedByDay(@Param("projectId") long projectId,@Param("from") Instant from,@Param("to") Instant to);
 @Query(value="select cast(done_tasks.first_completed at time zone 'UTC' as date) as day, count(*) as total from (select task_id, min(created_at) as first_completed from audit_logs where project_id=:projectId and action='STATUS_CHANGED' and new_value like '%\"status\":\"DONE\"%' and created_at>=:from and created_at<:to group by task_id) done_tasks group by cast(done_tasks.first_completed at time zone 'UTC' as date) order by day",nativeQuery=true)
 List<DayCount> countCompletedByDay(@Param("projectId") long projectId,@Param("from") Instant from,@Param("to") Instant to);
 long countByProjectId(long projectId);
 long countByProjectIdAndStatus(long projectId,TaskStatus status);
 long countByProjectIdAndDueDateBetweenAndStatusNot(long projectId,java.time.LocalDate from,java.time.LocalDate to,TaskStatus status);
 long countByProjectIdAndDueDateBeforeAndStatusNot(long projectId,java.time.LocalDate date,TaskStatus status);
}
