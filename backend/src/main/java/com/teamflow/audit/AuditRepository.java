package com.teamflow.audit;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuditRepository extends JpaRepository<AuditLog,Long> { List<AuditLog> findByTaskIdOrderByCreatedAtDesc(Long taskId); }
