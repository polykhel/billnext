@file:JvmName("SecurityUtils")

package com.polykhel.billnext.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

/**
 * Validate if the user entity is the same as the current user
 *
 * @param login login of user to validate
 * @return true if user is equal to current user login, otherwise false
 */
fun userIsCurrentUser(login: String?): Boolean {
    if (login != null) {
        val currentUserLogin = getCurrentUserLogin()

        if (currentUserLogin.isPresent) {
            return login == currentUserLogin.get()
        }
    }

    return false
}

/**
 * Get the login of the current user.
 *
 * @return the login of the current user.
 */
fun getCurrentUserLogin(): Optional<String> =
    Optional.ofNullable(extractPrincipal(SecurityContextHolder.getContext().authentication))

fun extractPrincipal(authentication: Authentication?): String? {

    if (authentication == null) {
        return null
    }

    return when (val principal = authentication.principal) {
        is UserDetails -> principal.username
        is String -> principal
        else -> null
    }
}

/**
 * Get the JWT of the current user.
 *
 * @return the JWT of the current user.
 */
fun getCurrentUserJWT(): Optional<String> =
    Optional.ofNullable(SecurityContextHolder.getContext().authentication)
        .filter { it.credentials is String }
        .map { it.credentials as String }

/**
 * Check if a user is authenticated.
 *
 * @return true if the user is authenticated, false otherwise.
 */
fun isAuthenticated(): Boolean {
    val authentication = SecurityContextHolder.getContext().authentication

    if (authentication != null) {
        val isAnonymousUser = getAuthorities(authentication)?.none { it == ANONYMOUS }
        if (isAnonymousUser != null) {
            return isAnonymousUser
        }
    }

    return false
}

/**
 * Checks if the current user has a specific authority.
 *
 * @param authority the authority to check.
 * @return true if the current user has the authority, false otherwise.
 */
fun hasCurrentUserThisAuthority(authority: String): Boolean {
    val authentication = SecurityContextHolder.getContext().authentication

    if (authentication != null) {
        val isUserPresent = getAuthorities(authentication)?.any { it == authority }
        if (isUserPresent != null) {
            return isUserPresent
        }
    }

    return false
}

fun getAuthorities(authentication: Authentication): List<String>? {
    return authentication.authorities.map(GrantedAuthority::getAuthority)
}
