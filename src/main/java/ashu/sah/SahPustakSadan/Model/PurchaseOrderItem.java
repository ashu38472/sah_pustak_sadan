package ashu.sah.SahPustakSadan.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "purchase_order_items", indexes = {
        @Index(name = "idx_po_item_po", columnList = "purchase_order_id"),
        @Index(name = "idx_po_item_product", columnList = "product_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class PurchaseOrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "ordered_quantity", nullable = false)
    private Integer orderedQuantity;

    @Column(name = "received_quantity", columnDefinition = "INTEGER DEFAULT 0")
    private Integer receivedQuantity = 0;

    @DecimalMin(value = "0.0", message = "Unit cost cannot be negative")
    @Column(name = "unit_cost", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double unitCost;

    @Column(name = "line_total", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double lineTotal;

    @PrePersist
    @PreUpdate
    private void calculateLineTotal() {
        if (orderedQuantity != null && unitCost != null) {
            lineTotal = orderedQuantity * unitCost;
        }
    }

    @Transient
    public Integer getPendingQuantity() {
        return orderedQuantity - receivedQuantity;
    }
}