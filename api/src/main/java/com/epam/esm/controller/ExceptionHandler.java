package com.epam.esm.controller;

import com.epam.esm.dao.exception.DaoException;
import com.epam.esm.dao.exception.GiftNotFoundException;
import com.epam.esm.dao.exception.TagNotFoundException;
import com.epam.esm.model.ErrorResponse;
import com.epam.esm.model.dto.GiftCertificateDto;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {
    private final ObjectMapper objectMapper;

    @Autowired
    public ExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {
            ServiceException.class,
            DaoException.class
    })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        ErrorResponse<Object> comment = ErrorResponse.builder()
                .code(Status.DEFAULT.getCode())
                .comment(Status.DEFAULT.getMessage()).build();
        return handleExceptionInternal(ex, comment, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(GiftNotFoundException.class)
    @SneakyThrows
    public void handleGiftNotFound(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        System.out.println("AT URI: " + request.getRequestURI() + " HANDLE EXCEPTION: " + ex);

        ErrorResponse<GiftCertificateDto> body = ErrorResponse.<GiftCertificateDto>builder()
                .code(Status.GIFT_NOT_FOUND.getCode())
                .comment(Status.GIFT_NOT_FOUND.getMessage()).build();

        response.setStatus(HttpStatus.INSUFFICIENT_STORAGE.value());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(TagNotFoundException.class)
    @SneakyThrows
    public void handleTagNotFound(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        System.out.println("AT URI: " + request.getRequestURI() + " HANDLE EXCEPTION: " + ex);

        ErrorResponse<GiftCertificateDto> body = ErrorResponse.<GiftCertificateDto>builder()
                .code(Status.TAG_NOT_FOUND.getCode())
                .comment(Status.TAG_NOT_FOUND.getMessage()).build();

        response.setStatus(HttpStatus.INSUFFICIENT_STORAGE.value());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
