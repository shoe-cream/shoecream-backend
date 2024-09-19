package com.springboot.auth.handler;

import com.google.gson.Gson;
import com.springboot.auth.utils.ErrorResponder;
import com.springboot.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MemberAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        if (exception instanceof BadCredentialsException) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid username or password");
        } else if (exception instanceof InternalAuthenticationServiceException) {
            sendErrorResponse(response, HttpStatus.NOT_FOUND, "Member Not Found");
        } else {
            sendErrorResponse(response, HttpStatus.FORBIDDEN, "Unauthorized");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        Gson gson = new Gson();
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.getWriter().write(gson.toJson(errorResponse, ErrorResponse.class));
    }

}
