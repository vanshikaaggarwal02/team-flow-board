package com.teamflow.common;
import java.util.*;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestControllerAdvice
public class ApiErrors {
 private static final Logger log=LoggerFactory.getLogger(ApiErrors.class);
 @ExceptionHandler(ApiException.class) ResponseEntity<?> api(ApiException e){return ResponseEntity.status(e.status).body(Map.of("code",e.code,"message",e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException e){var fields=new LinkedHashMap<String,String>();e.getBindingResult().getFieldErrors().forEach(x->fields.putIfAbsent(x.getField(),x.getDefaultMessage()));return ResponseEntity.badRequest().body(Map.of("code","VALIDATION_ERROR","message","Check the highlighted fields.","fields",fields));}
 @ExceptionHandler({ConstraintViolationException.class,IllegalArgumentException.class}) ResponseEntity<?> invalid(Exception e){return ResponseEntity.badRequest().body(Map.of("code","INVALID_REQUEST","message","The request contains invalid values."));}
 @ExceptionHandler({MethodArgumentTypeMismatchException.class,HttpMessageNotReadableException.class}) ResponseEntity<?> unreadable(Exception e){return ResponseEntity.badRequest().body(Map.of("code","INVALID_REQUEST","message","The request contains invalid values."));}
 @ExceptionHandler({ObjectOptimisticLockingFailureException.class,DataIntegrityViolationException.class}) ResponseEntity<?> conflict(Exception e){return ResponseEntity.status(409).body(Map.of("code","CONFLICT","message","This item changed or conflicts with existing data. Refresh and try again."));}
 @ExceptionHandler(Exception.class) ResponseEntity<?> internal(Exception e){log.error("Unhandled API exception",e);return ResponseEntity.status(500).body(Map.of("code","INTERNAL_ERROR","message","Something went wrong. Please try again."));}
}
