package ashu.sah.SahPustakSadan.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "customer_types", indexes = {
        @Index(name = "idx_customer_type_name", columnList = "name")
})
@Where(clause = "deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerType extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer type name is required")
    @Column(unique = true, nullable = false)
    private String name; // REGULAR, IRREGULAR, SHOPKEEPER, SCHOOL, TEACHER

    private String description;

    @Column(name = "default_discount_percentage", columnDefinition = "DECIMAL(5,2) DEFAULT 0.00")
    private Double defaultDiscountPercentage = 0.0;

    @Column(name = "default_credit_limit", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double defaultCreditLimit = 0.0;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;
}