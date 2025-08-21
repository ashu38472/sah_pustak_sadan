package ashu.sah.SahPustakSadan.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_prices", indexes = {
        @Index(name = "idx_product_price_product", columnList = "product_id"),
        @Index(name = "idx_product_price_customer_type", columnList = "customer_type_id"),
        @Index(name = "idx_product_price_effective_date", columnList = "effective_date"),
        @Index(name = "idx_product_price_active", columnList = "is_active")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductPrice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_type_id", nullable = false)
    private CustomerType customerType;

    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double price;

    @Column(name = "effective_date", nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @PrePersist
    @PreUpdate
    private void setDefaultEffectiveDate() {
        if (effectiveDate == null) {
            effectiveDate = LocalDateTime.now();
        }
    }
}