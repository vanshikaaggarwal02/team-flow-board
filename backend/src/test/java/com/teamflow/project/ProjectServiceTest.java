package com.teamflow.project;
import com.teamflow.common.ApiException;
import com.teamflow.user.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
 @Mock ProjectRepository projects;
 @Mock ProjectMemberRepository members;
 @Mock com.teamflow.user.UserRepository users;
 @InjectMocks ProjectService service;
 @Test void hidesProjectFromNonMembers(){
  User owner=new User("Owner","owner@example.com","hash");owner.id=1L;
  Project project=new Project("TeamFlow",null,owner);project.id=9L;
  User visitor=new User("Visitor","visitor@example.com","hash");visitor.id=2L;
  when(projects.findById(9L)).thenReturn(Optional.of(project));
  when(members.findByProjectIdAndUserId(9L,2L)).thenReturn(Optional.empty());
  ApiException error=assertThrows(ApiException.class,()->service.get(visitor,9L));
  assertEquals(HttpStatus.NOT_FOUND,error.status);
 }
 @Test void restrictsProjectSettingsToOwner(){
  User owner=new User("Owner","owner@example.com","hash");owner.id=1L;
  User member=new User("Member","member@example.com","hash");member.id=2L;
  Project project=new Project("TeamFlow",null,owner);project.id=9L;
  when(projects.findById(9L)).thenReturn(Optional.of(project));
  when(members.findByProjectIdAndUserId(9L,2L)).thenReturn(Optional.of(new ProjectMember(9L,2L,ProjectRole.MEMBER)));
  ApiException error=assertThrows(ApiException.class,()->service.update(member,9L,"Changed",null));
  assertEquals(HttpStatus.FORBIDDEN,error.status);
 }
 @Test void adminCanRemoveProjectMember(){
  User admin=new User("Admin","admin@example.com","hash");admin.id=3L;
  var adminMembership=new ProjectMember(9L,3L,ProjectRole.ADMIN);
  var target=new ProjectMember(9L,2L,ProjectRole.MEMBER);
  when(members.findByProjectIdAndUserId(9L,3L)).thenReturn(Optional.of(adminMembership));
  when(members.findByProjectIdAndUserId(9L,2L)).thenReturn(Optional.of(target));
  service.removeMember(admin,9L,2L);
  verify(members).delete(target);
 }
 @Test void projectOwnerCannotBeRemoved(){
  User admin=new User("Admin","admin@example.com","hash");admin.id=3L;
  var adminMembership=new ProjectMember(9L,3L,ProjectRole.ADMIN);
  var ownerMembership=new ProjectMember(9L,1L,ProjectRole.OWNER);
  when(members.findByProjectIdAndUserId(9L,3L)).thenReturn(Optional.of(adminMembership));
  when(members.findByProjectIdAndUserId(9L,1L)).thenReturn(Optional.of(ownerMembership));
  ApiException error=assertThrows(ApiException.class,()->service.removeMember(admin,9L,1L));
  assertEquals(HttpStatus.FORBIDDEN,error.status);
  verify(members,never()).delete(any(ProjectMember.class));
 }

}
