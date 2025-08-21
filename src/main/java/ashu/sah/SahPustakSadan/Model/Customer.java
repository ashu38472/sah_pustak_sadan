package ashu.sah.SahPustakSadan.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_phone", columnList = "phone"),
        @Index(name = "idx_customer_email", columnList = "email"),
        @Index(name = "idx_customer_type", columnList = "type_id"),
        @Index(name = "idx_customer_active", columnList = "is_active")
})
@Where(clause = "deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private CustomerType type;

    @NotBlank(message = "Customer name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Column(nullable = false)
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String address;

    @DecimalMin(value = "0.0", message = "Credit limit cannot be negative")
    @Column(name = "credit_limit", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double creditLimit = 0.0;

    @DecimalMin(value = "0.0", message = "Credit balance cannot be negative")
    @Column(name = "credit_balance", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double creditBalance = 0.0;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @PrePersist
    @PreUpdate
    private void validateBusinessRules() {
        if (creditBalance != null && creditLimit != null && creditBalance > creditLimit) {
            throw new IllegalStateException("Credit balance cannot exceed credit limit");
        }
    }
}