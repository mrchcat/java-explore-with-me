package com.github.mrchcat.explorewithme.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserCreateDto {
    @Email(message = "incorrect email")
    @NotNull(message = "email can not be null")
    @Length(min = 6, max = 254, message = "Name must have size 6-254 signs.")
    private String email;

    @NotBlank(message = "name can not be blank")
    @Length(min = 2, max = 250, message = "Name must have size 2-250 signs.")
    private String name;
}
