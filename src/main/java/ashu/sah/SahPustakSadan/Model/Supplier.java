package ashu.sah.SahPustakSadan.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "suppliers", indexes = {
        @Index(name = "idx_supplier_name", columnList = "name"),
        @Index(name = "idx_supplier_phone", columnList = "phone"),
        @Index(name = "idx_supplier_active", columnList = "is_active")
})
@Where(clause = "deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
public class Supplier extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Supplier name is required")
    @Column(nullable = false)
    private String name;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Column(nullable = false)
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "tax_number")
    private String taxNumber;

    @Column(name = "credit_limit", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double creditLimit = 0.0;

    @Column(name = "credit_balance", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double creditBalance = 0.0;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    private String notes;
}