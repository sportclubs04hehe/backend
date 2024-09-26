package fpt.edu.vn.backend.auth;

import fpt.edu.vn.backend.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @NotNull
    private String firstName;

    @NotBlank
    @NotNull
    private String lastName;

    @NotBlank
    @NotNull
    @Email
    private String email;

    @NotBlank
    @NotNull
    private String pass;

    @NotNull
    private LocalDate dateOfBirth;

    @NotBlank
    @NotNull
    private String knowAs;

    @NotBlank
    @NotNull
    private String gender;

    @NotBlank
    @NotNull
    private Role role;
}

