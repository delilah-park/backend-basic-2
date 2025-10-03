package kr.sparta.backendbasic2.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "접근 권한이 없습니다");
        response.put("message", "관리자 권한이 필요한 기능입니다");
        response.put("status", "403");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}