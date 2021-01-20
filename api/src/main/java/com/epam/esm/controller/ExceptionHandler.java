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
import org.springframework.context.MessageSource;
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
    private final MessageSource messageSource;

    @Autowired
    public ExceptionHandler(ObjectMapper objectMapper, MessageSource messageSource) {
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {
            ServiceException.class,
            DaoException.class
    })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest webRequest) {
        String message = messageSource.getMessage(Status.DEFAULT.getCode().toString(), null, webRequest.getLocale());

        ErrorResponse<Object> comment = ErrorResponse.builder()
                .code(Status.DEFAULT.getCode())
                .comment(message).build();
        return handleExceptionInternal(ex, comment, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(GiftNotFoundException.class)
    @SneakyThrows
    public void handleGiftNotFound(HttpServletRequest request, HttpServletResponse response, Exception ex, WebRequest webRequest) {
        System.out.println("AT URI: " + request.getRequestURI() + " HANDLE EXCEPTION: " + ex);

        String message = messageSource.getMessage(Status.GIFT_NOT_FOUND.getCode().toString(), null, webRequest.getLocale());

        ErrorResponse<GiftCertificateDto> body = ErrorResponse.<GiftCertificateDto>builder()
                .code(Status.GIFT_NOT_FOUND.getCode())
                .comment(message).build();

        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.INSUFFICIENT_STORAGE.value());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(TagNotFoundException.class)
    @SneakyThrows
    public void handleTagNotFound(HttpServletRequest request, HttpServletResponse response, Exception ex, WebRequest webRequest) {
        System.out.println("AT URI: " + request.getRequestURI() + " HANDLE EXCEPTION: " + ex);

        String message = messageSource.getMessage(Status.TAG_NOT_FOUND.getCode().toString(), null, webRequest.getLocale());

        ErrorResponse<GiftCertificateDto> body = ErrorResponse.<GiftCertificateDto>builder()
                .code(Status.TAG_NOT_FOUND.getCode())
                .comment(message).build();

        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.INSUFFICIENT_STORAGE.value());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
