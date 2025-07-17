package com.example.userapi.model;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
}
