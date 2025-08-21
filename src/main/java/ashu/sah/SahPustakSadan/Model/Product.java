package ashu.sah.SahPustakSadan.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_code", columnList = "code"),
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_category", columnList = "category_id"),
        @Index(name = "idx_product_barcode", columnList = "barcode"),
        @Index(name = "idx_product_active", columnList = "is_active")
})
@Where(clause = "deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotBlank(message = "Product code is required")
    @Column(unique = true, nullable = false)
    private String code;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    @Column(unique = true)
    private String barcode;

    @NotBlank(message = "Unit is required")
    @Column(nullable = false)
    private String unit;

    @DecimalMin(value = "0.0", message = "Base price cannot be negative")
    @Column(name = "base_price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double basePrice;

    @DecimalMin(value = "0.0", message = "Cost price cannot be negative")
    @Column(name = "cost_price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double costPrice;

    @Min(value = 0, message = "Minimum stock level cannot be negative")
    @Column(name = "min_stock_level", columnDefinition = "INTEGER DEFAULT 0")
    private Integer minStockLevel = 0;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(name = "tax_percentage", columnDefinition = "DECIMAL(5,2) DEFAULT 0.00")
    private Double taxPercentage = 0.0;
}