package com.teamflow.task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class TaskStatusTest {
 @Test void allowsMovingBetweenAnyDistinctBoardColumns(){
  for(TaskStatus current:TaskStatus.values()){
   for(TaskStatus target:TaskStatus.values()){
    assertEquals(current!=target,current.canMoveTo(target),current+" -> "+target);
   }
  }
  assertFalse(TaskStatus.TODO.canMoveTo(null));
 }
}
