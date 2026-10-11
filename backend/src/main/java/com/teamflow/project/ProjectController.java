package com.teamflow.project;
import com.teamflow.common.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/projects")
public class ProjectController {
 private final ProjectService service;private final ProjectInsightsService insights;private final CurrentUser current;
 public ProjectController(ProjectService s,ProjectInsightsService insights,CurrentUser c){service=s;this.insights=insights;current=c;}
 record ProjectRequest(@NotBlank @Size(max=120) String name,@Size(max=2000) String description){}
 record AddMember(@NotBlank @Email String email,@NotNull ProjectRole role){}
 record RoleRequest(@NotNull ProjectRole role){}
 @GetMapping List<ProjectService.View> list(Authentication a){return service.list(current.get(a));}
 @PostMapping ProjectService.View create(Authentication a,@Valid @RequestBody ProjectRequest r){return service.create(current.get(a),r.name(),r.description());}
 @GetMapping("/{id}") ProjectService.View get(Authentication a,@PathVariable long id){return service.get(current.get(a),id);}
 @GetMapping("/{id}/overview") ProjectInsightsService.Overview overview(Authentication a,@PathVariable long id){return insights.overview(current.get(a),id);}
 @GetMapping("/{id}/analytics") ProjectInsightsService.Analytics analytics(Authentication a,@PathVariable long id,@RequestParam(required=false) java.time.LocalDate from,@RequestParam(required=false) java.time.LocalDate to){return insights.analytics(current.get(a),id,from,to);}
 @PatchMapping("/{id}") ProjectService.View update(Authentication a,@PathVariable long id,@Valid @RequestBody ProjectRequest r){return service.update(current.get(a),id,r.name(),r.description());}
 @GetMapping("/{id}/members") List<ProjectService.MemberView> members(Authentication a,@PathVariable long id){return service.listMembers(current.get(a),id);}
 @PostMapping("/{id}/members") ProjectService.MemberView add(Authentication a,@PathVariable long id,@Valid @RequestBody AddMember r){return service.addMember(current.get(a),id,r.email(),r.role());}
 @PatchMapping("/{id}/members/{userId}") ProjectService.MemberView change(Authentication a,@PathVariable long id,@PathVariable long userId,@Valid @RequestBody RoleRequest r){return service.changeRole(current.get(a),id,userId,r.role());}
 @DeleteMapping("/{id}/members/{userId}") @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT) void remove(Authentication a,@PathVariable long id,@PathVariable long userId){service.removeMember(current.get(a),id,userId);}
}
