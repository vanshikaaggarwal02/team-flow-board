package com.teamflow.project;
import com.teamflow.common.*;
import com.teamflow.user.*;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ProjectService {
 private final ProjectRepository projects; private final ProjectMemberRepository members; private final UserRepository users;
 public ProjectService(ProjectRepository p,ProjectMemberRepository m,UserRepository u){projects=p;members=m;users=u;}
 public record View(Long id,String name,String description,String role,int memberCount,Instant updatedAt){}
 public record MemberView(Long id,String name,String email,String role){}
 @Transactional(readOnly=true) public List<View> list(User actor){return members.findByUserId(actor.id).stream().map(m->projects.findById(m.projectId).map(p->view(p,m.role)).orElse(null)).filter(Objects::nonNull).toList();}
 @Transactional public View create(User actor,String name,String description){Project p=projects.save(new Project(name.trim(),description,actor));members.save(new ProjectMember(p.id,actor.id,ProjectRole.OWNER));return view(p,ProjectRole.OWNER);}
 @Transactional(readOnly=true) public View get(User actor,long id){Project p=projects.findById(id).orElseThrow(ApiException::notFound);ProjectMember m=membership(id,actor.id);return view(p,m.role);}
 @Transactional public View update(User actor,long id,String name,String description){Project p=projects.findById(id).orElseThrow(ApiException::notFound);ProjectMember m=membership(id,actor.id);if(m.role!=ProjectRole.OWNER)throw ApiException.forbidden();if(name!=null)p.name=name.trim();p.description=description;p.updatedAt=Instant.now();return view(p,m.role);}
 @Transactional(readOnly=true) public List<MemberView> listMembers(User actor,long project){membership(project,actor.id);return members.findByProjectId(project).stream().map(m->{var u=users.findById(m.userId).orElseThrow();return new MemberView(u.id,u.name,u.email,m.role.name());}).toList();}
 @Transactional public MemberView addMember(User actor,long project,String email,ProjectRole role){ProjectMember me=membership(project,actor.id);if(me.role==ProjectRole.MEMBER)throw ApiException.forbidden();if(role==ProjectRole.OWNER)throw ApiException.badRequest("Ownership cannot be assigned through membership management.");User user=users.findByEmail(email.trim().toLowerCase()).orElseThrow(ApiException::notFound);if(members.existsById(new ProjectMemberKey(project,user.id)))throw ApiException.conflict("This user is already a project member.");members.save(new ProjectMember(project,user.id,role));return new MemberView(user.id,user.name,user.email,role.name());}
 @Transactional public MemberView changeRole(User actor,long project,long userId,ProjectRole role){ProjectMember me=membership(project,actor.id);if(me.role!=ProjectRole.OWNER&&me.role!=ProjectRole.ADMIN)throw ApiException.forbidden();if(role==ProjectRole.OWNER)throw ApiException.badRequest("Ownership transfer is not available.");ProjectMember target=members.findByProjectIdAndUserId(project,userId).orElseThrow(ApiException::notFound);if(target.role==ProjectRole.OWNER)throw ApiException.forbidden();target.role=role;var u=users.findById(userId).orElseThrow();return new MemberView(u.id,u.name,u.email,role.name());}
 @Transactional public void removeMember(User actor,long project,long userId){ProjectMember me=membership(project,actor.id);if(me.role!=ProjectRole.OWNER&&me.role!=ProjectRole.ADMIN)throw ApiException.forbidden();ProjectMember target=members.findByProjectIdAndUserId(project,userId).orElseThrow(ApiException::notFound);if(target.role==ProjectRole.OWNER)throw ApiException.forbidden();members.delete(target);}
 public ProjectMember membership(long project,long user){return members.findByProjectIdAndUserId(project,user).orElseThrow(ApiException::notFound);}
 private View view(Project p,ProjectRole role){return new View(p.id,p.name,p.description,role.name(),members.findByProjectId(p.id).size(),p.updatedAt);}
}
