package com.teamflow.project;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProjectMemberRepository extends JpaRepository<ProjectMember,ProjectMemberKey> {
 Optional<ProjectMember> findByProjectIdAndUserId(Long projectId,Long userId);
 List<ProjectMember> findByUserId(Long userId);
 List<ProjectMember> findByProjectId(Long projectId);
}
