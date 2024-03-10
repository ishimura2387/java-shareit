package ru.practicum.shareit.user;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "users", schema = "public")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String email;
}
