package com.teamflow.project;
import java.io.Serializable;
import java.util.Objects;
public class ProjectMemberKey implements Serializable {
 public Long projectId; public Long userId;
 public ProjectMemberKey() {}
 public ProjectMemberKey(Long projectId,Long userId){this.projectId=projectId;this.userId=userId;}
 public boolean equals(Object o){return o instanceof ProjectMemberKey k&&Objects.equals(projectId,k.projectId)&&Objects.equals(userId,k.userId);}
 public int hashCode(){return Objects.hash(projectId,userId);}
}
