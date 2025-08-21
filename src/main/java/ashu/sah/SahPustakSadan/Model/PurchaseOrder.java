package ashu.sah.SahPustakSadan.Model;

import ashu.sah.SahPustakSadan.enums.PurchaseOrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders", indexes = {
        @Index(name = "idx_po_supplier", columnList = "supplier_id"),
        @Index(name = "idx_po_user", columnList = "user_id"),
        @Index(name = "idx_po_status", columnList = "status"),
        @Index(name = "idx_po_date", columnList = "order_date")
})
@Where(clause = "deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
public class PurchaseOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "po_number", unique = true, nullable = false)
    private String poNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseOrderStatus status;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @DecimalMin(value = "0.0", message = "Total amount cannot be negative")
    @Column(name = "total_amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double totalAmount;

    @Column(name = "tax_amount", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double taxAmount = 0.0;

    @Column(name = "discount_amount", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double discountAmount = 0.0;

    @Column(name = "final_amount", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double finalAmount = 0.0;

    private String notes;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void calculateAmounts() {
        if (totalAmount != null) {
            finalAmount = totalAmount + (taxAmount != null ? taxAmount : 0.0)
                    - (discountAmount != null ? discountAmount : 0.0);
        }
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
    }
}