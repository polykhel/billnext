package com.polykhel.billnext.service.dto;

import com.polykhel.billnext.domain.User;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO representing a user, with only the public attributes.
 */
@Getter
@Setter
public class UserDTO {

    private String id;

    private String login;

    public UserDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserDTO(User user) {
        this.id = user.getId();
        // Customize it here if you need, or not, firstName/lastName/etc
        this.login = user.getLogin();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDTO{" +
            "id='" + id + '\'' +
            ", login='" + login + '\'' +
            "}";
    }
}
