
package com.schoollaboratory.model;

public record User(
        int id,
        String name,
        String username,
        String password,
        String role,
        String status
) {
}
