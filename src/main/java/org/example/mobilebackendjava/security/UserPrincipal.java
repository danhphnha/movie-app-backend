package org.example.mobilebackendjava.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class UserPrincipal implements Serializable {
    private final String uid;
    private final String email;
    private final String name;
    private final boolean admin;
}
