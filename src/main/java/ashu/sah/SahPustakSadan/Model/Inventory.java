package ashu.sah.SahPustakSadan.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "inventory", indexes = {
        @Index(name = "idx_inventory_product", columnList = "product_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class Inventory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer quantity = 0;

    @Min(value = 0, message = "Reserved quantity cannot be negative")
    @Column(name = "reserved_quantity", columnDefinition = "INTEGER DEFAULT 0")
    private Integer reservedQuantity = 0;

    @Transient
    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    @Transient
    public boolean isLowStock() {
        return product != null && quantity <= product.getMinStockLevel();
    }
}