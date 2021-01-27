package com.epam.esm.controller;

import com.epam.esm.dao.exception.DaoException;
import com.epam.esm.dao.exception.GiftNotFoundException;
import com.epam.esm.dao.exception.TagNameRegisteredException;
import com.epam.esm.dao.exception.TagNotFoundException;
import com.epam.esm.model.ErrorResponse;
import com.epam.esm.model.dto.GiftCertificateDto;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
public class DefaultExceptionHandler {
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    private static final String SEARCH_ENTITY_ID_REGEX = "^.+/";

    @Autowired
    public DefaultExceptionHandler(ObjectMapper objectMapper, MessageSource messageSource) {
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }

    @ExceptionHandler(value = {
            ServiceException.class,
            DaoException.class
    })
    public ResponseEntity<ErrorResponse<Object>> handleConflict(RuntimeException ex, WebRequest webRequest) {
        String message = messageSource.getMessage(Status.DEFAULT.getCode().toString(), null, webRequest.getLocale());

        ErrorResponse<Object> comment = ErrorResponse.builder()
                .code(Status.DEFAULT.getCode())
                .comment(message).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(comment);
    }

    @ExceptionHandler(GiftNotFoundException.class)
    @SneakyThrows
    public void handleGiftNotFound(HttpServletRequest request, HttpServletResponse response, Exception ex, WebRequest webRequest) {
        String requestURI = request.getRequestURI();
        String requestedId = requestURI.replaceAll(SEARCH_ENTITY_ID_REGEX, "");

        System.out.println("AT URI: " + requestURI + " HANDLE EXCEPTION: " + ex);

        String message = messageSource.getMessage(Status.GIFT_NOT_FOUND.getCode().toString(), null, webRequest.getLocale());

        ErrorResponse<GiftCertificateDto> body = ErrorResponse.<GiftCertificateDto>builder()
                .code(Status.GIFT_NOT_FOUND.getCode())
                .comment(message + " (id) = " + requestedId).build();

        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    @ExceptionHandler(TagNotFoundException.class)
    @SneakyThrows
    public void handleTagNotFound(HttpServletRequest request, HttpServletResponse response, Exception ex, WebRequest webRequest) {
        String requestURI = request.getRequestURI();
        String requestedId = requestURI.replaceAll(SEARCH_ENTITY_ID_REGEX, "");
        System.out.println("AT URI: " + requestURI + " HANDLE EXCEPTION: " + ex);

        String message = messageSource.getMessage(Status.TAG_NOT_FOUND.getCode().toString(), null, webRequest.getLocale());

        ErrorResponse<GiftCertificateDto> body = ErrorResponse.<GiftCertificateDto>builder()
                .code(Status.TAG_NOT_FOUND.getCode())
                .comment(message + " (id) = " + requestedId).build();

        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    @ExceptionHandler(TagNameRegisteredException.class)
    @SneakyThrows
    public void handleTagNameRegistered(HttpServletRequest request, HttpServletResponse response, Exception ex, WebRequest webRequest) {
        System.out.println("AT URI: " + request.getRequestURI() + " HANDLE EXCEPTION: " + ex);

        String message = messageSource.getMessage(Status.TAG_NAME_ALREADY_REGISTERED.getCode().toString(), null, webRequest.getLocale());

        ErrorResponse<GiftCertificateDto> body = ErrorResponse.<GiftCertificateDto>builder()
                .code(Status.TAG_NAME_ALREADY_REGISTERED.getCode())
                .comment(message).build();

        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse<Object>>> validationErrorHandler(MethodArgumentNotValidException e) {
        List<ErrorResponse<Object>> collect = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ErrorResponse.builder()
                        .code(Status.VALIDATION_EXCEPTION.getCode())
                        .comment(fieldError.getField() + ": " + fieldError.getDefaultMessage()).build())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(collect);
    }
}
