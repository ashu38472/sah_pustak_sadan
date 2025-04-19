package ashu.sah.SahPustakSadan.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user")
public class User {
    // Getters & Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role; // Enum for role

    public enum Role {
        admin, staff, manager
    }
}
