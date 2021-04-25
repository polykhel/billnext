package com.polykhel.billnext.web.rest.errors;

import org.springframework.security.access.AccessDeniedException;

public class ForbiddenException extends AccessDeniedException {

    public ForbiddenException() {
        super("Access Denied");
    }
}
