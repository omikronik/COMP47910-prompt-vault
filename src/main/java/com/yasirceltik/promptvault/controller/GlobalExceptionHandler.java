package com.yasirceltik.promptvault.controller;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import com.yasirceltik.promptvault.exception.ConversationNotFoundException;
import com.yasirceltik.promptvault.exception.InvalidPromptCategoryException;
import com.yasirceltik.promptvault.exception.PromptAccessException;
import com.yasirceltik.promptvault.exception.PromptNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    public static final String CORRELATION_HEADER = "X-Correlation-ID";

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            InvalidPromptCategoryException.class
    })
    public ModelAndView handleBadRequest(Exception exception, HttpServletResponse response) {
        return errorResponse(HttpStatus.BAD_REQUEST, exception, response);
    }

    @ExceptionHandler({
            PromptNotFoundException.class,
            PromptAccessException.class,
            ConversationNotFoundException.class,
            NoSuchElementException.class
    })
    public ModelAndView handleNotFound(Exception exception, HttpServletResponse response) {
        return errorResponse(HttpStatus.NOT_FOUND, exception, response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleForbidden(Exception exception, HttpServletResponse response) {
        return errorResponse(HttpStatus.FORBIDDEN, exception, response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleConflict(Exception exception, HttpServletResponse response) {
        return errorResponse(HttpStatus.CONFLICT, exception, response);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpected(Exception exception, HttpServletResponse response) {
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception, response);
    }

    private ModelAndView errorResponse(
            HttpStatus status,
            Exception exception,
            HttpServletResponse response) {
        String correlationId = UUID.randomUUID().toString();

        response.setStatus(status.value());
        response.setHeader(CORRELATION_HEADER, correlationId);

        if (status.is5xxServerError()) {
            log.error("request failed correlationId={} status={} exceptionType={}",
                    correlationId, status.value(), exception.getClass().getName());
        } else {
            log.warn("request rejected correlationId={} status={} exceptionType={}",
                    correlationId, status.value(), exception.getClass().getName());
        }

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.setStatus(status);
        modelAndView.addObject("status", status.value());
        modelAndView.addObject("correlationId", correlationId);
        return modelAndView;
    }
}
