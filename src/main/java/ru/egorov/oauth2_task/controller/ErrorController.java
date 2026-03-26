package ru.egorov.oauth2_task.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class ErrorController {
    @GetMapping("/error")
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {

        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String exceptionMessage = (String) request.getAttribute("jakarta.servlet.error.message");
        String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");

        if (statusCode == null) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        String authError = (String) request.getSession().getAttribute("error.message");
        if (authError != null) {
            request.getSession().removeAttribute("error.message");
            exceptionMessage = authError;
        }

        log.warn("ОШИБКА: status={}, uri={}, message={}", statusCode, requestUri, exceptionMessage);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", statusCode);
        errorResponse.put("error", HttpStatus.valueOf(statusCode).getReasonPhrase());
        errorResponse.put("message", exceptionMessage != null ? exceptionMessage : "Произошла ошибка");
        errorResponse.put("path", requestUri);

        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}
