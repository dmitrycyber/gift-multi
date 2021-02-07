package com.epam.esm.controller;

import com.epam.esm.dao.exception.DaoException;
import com.epam.esm.dao.exception.GiftNotFoundException;
import com.epam.esm.dao.exception.TagNameRegisteredException;
import com.epam.esm.dao.exception.TagNotFoundException;
import com.epam.esm.model.ErrorResponse;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class DefaultExceptionHandler {
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Autowired
    public DefaultExceptionHandler(ObjectMapper objectMapper, MessageSource messageSource) {
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }

    @ExceptionHandler(value = {
            ServiceException.class,
            DaoException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex, WebRequest webRequest, HttpServletRequest request) {
        String message = messageSource.getMessage(Status.DEFAULT.getCode().toString(), null, webRequest.getLocale());

        return getErrorResponseResponseEntity(request, ex, webRequest, Status.DEFAULT, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    @ExceptionHandler(GiftNotFoundException.class)
    @SneakyThrows
    public ResponseEntity<ErrorResponse> handleGiftNotFound(HttpServletRequest request, HttpServletResponse response, Exception ex, WebRequest webRequest) {
        String message = messageSource.getMessage(Status.GIFT_NOT_FOUND.getCode().toString(), null, webRequest.getLocale());

        String resultMessage = errorMessageNotFoundById(message, ex);

        return getErrorResponseResponseEntity(request, ex, webRequest, Status.GIFT_NOT_FOUND, HttpStatus.NOT_FOUND, resultMessage);
    }

    @ExceptionHandler(TagNotFoundException.class)
    @SneakyThrows
    public ResponseEntity<ErrorResponse> handleTagNotFound(HttpServletRequest request, HttpServletResponse response, Exception ex, WebRequest webRequest) {
        String message = messageSource.getMessage(Status.TAG_NOT_FOUND.getCode().toString(), null, webRequest.getLocale());

        String resultMessage = errorMessageNotFoundById(message, ex);

        return getErrorResponseResponseEntity(request, ex, webRequest, Status.TAG_NOT_FOUND, HttpStatus.NOT_FOUND, resultMessage);
    }

    @ExceptionHandler(TagNameRegisteredException.class)
    @SneakyThrows
    public ResponseEntity<ErrorResponse> handleTagNameRegistered(HttpServletRequest request, HttpServletResponse response, Exception ex, WebRequest webRequest) {
        String message = messageSource.getMessage(Status.TAG_NAME_ALREADY_REGISTERED.getCode().toString(), null, webRequest.getLocale());

        return getErrorResponseResponseEntity(request, ex, webRequest, Status.TAG_NAME_ALREADY_REGISTERED, HttpStatus.CONFLICT, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> validationErrorHandler(MethodArgumentNotValidException e) {
        List<ErrorResponse> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ErrorResponse.builder()
                        .code(Status.VALIDATION_EXCEPTION.getCode())
                        .comment(fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }


    private ResponseEntity<ErrorResponse> getErrorResponseResponseEntity(HttpServletRequest request, Exception ex, WebRequest webRequest, Status status, HttpStatus httpStatus, String message) {
        log.error("AT URI: " + request.getRequestURI() + " HANDLE EXCEPTION: " + ex);

        ErrorResponse body = ErrorResponse.builder()
                .code(status.getCode())
                .comment(message)
                .build();

        return ResponseEntity.status(httpStatus).body(body);
    }

    private String errorMessageNotFoundById(String message, Exception ex) {
        return message + " (id) = " + ex.getMessage();
    }
}
