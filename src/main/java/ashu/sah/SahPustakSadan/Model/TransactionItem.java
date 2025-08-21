package ashu.sah.SahPustakSadan.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "transaction_items", indexes = {
        @Index(name = "idx_transaction_item_transaction", columnList = "transaction_id"),
        @Index(name = "idx_transaction_item_product", columnList = "product_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    @DecimalMin(value = "0.0", message = "Unit price cannot be negative")
    @Column(name = "unit_price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double unitPrice;

    @DecimalMin(value = "0.0", message = "Cost price cannot be negative")
    @Column(name = "cost_price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double costPrice;

    @Column(name = "line_total", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double lineTotal;

    @Column(name = "discount_amount", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double discountAmount = 0.0;

    @Column(name = "tax_amount", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double taxAmount = 0.0;

    @PrePersist
    @PreUpdate
    private void calculateLineTotal() {
        if (quantity != null && unitPrice != null) {
            lineTotal = quantity * unitPrice;
            if (discountAmount != null) {
                lineTotal -= discountAmount;
            }
            if (taxAmount != null) {
                lineTotal += taxAmount;
            }
        }
    }
}