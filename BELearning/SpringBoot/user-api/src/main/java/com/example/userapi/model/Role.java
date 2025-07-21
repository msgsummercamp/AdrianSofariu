package com.example.userapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Role {
    @Id
    private String name;

    @OneToMany(mappedBy = "role")
    private Set<User> users;
}