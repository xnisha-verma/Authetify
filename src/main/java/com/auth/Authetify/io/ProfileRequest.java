package com.auth.Authetify.io;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class ProfileRequest {
    @Email(message = "Enter valid email address")
    @NotNull(message = "Email should not be empty")
    private String email;
    @NotBlank(message = "Name should not be empty")
    private String name;
    @Size(min = 6 , message = "Password must be 6 characters")
    private String password;
}
