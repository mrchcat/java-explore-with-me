package com.github.mrchcat.explorewithme.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserCreateDto {
    @Email(message = "incorrect email")
    @NotNull(message = "email can not be null")
    private String email;

    @NotBlank(message = "name can not be blank")
    private String name;
}
