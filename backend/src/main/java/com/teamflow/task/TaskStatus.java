package com.teamflow.task;
import java.util.*;
public enum TaskStatus {
 TODO, IN_PROGRESS, IN_REVIEW, DONE;
 public boolean canMoveTo(TaskStatus target){return target!=null&&target!=this;}
}
