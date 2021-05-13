package com.polykhel.billnext.web.rest.errors

import org.springframework.security.access.AccessDeniedException

class ForbiddenException: AccessDeniedException("You are not allowed to access this resource")
