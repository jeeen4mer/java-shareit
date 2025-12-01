package ru.practicum.shareit.user.model;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Size(min = 1, max = 20)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
}
