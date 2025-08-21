package ashu.sah.SahPustakSadan.Model;

import ashu.sah.SahPustakSadan.enums.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transaction_customer", columnList = "customer_id"),
        @Index(name = "idx_transaction_user", columnList = "user_id"),
        @Index(name = "idx_transaction_date", columnList = "transaction_date"),
        @Index(name = "idx_transaction_status", columnList = "status")
})
@Where(clause = "deleted_at IS NULL")
@Data
@EqualsAndHashCode(callSuper = true)
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @DecimalMin(value = "0.0", message = "Total amount cannot be negative")
    @Column(name = "total_amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double totalAmount;

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    @Column(name = "discount_amount", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double discountAmount = 0.0;

    @DecimalMin(value = "0.0", message = "Total cost cannot be negative")
    @Column(name = "total_cost", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double totalCost = 0.0;

    @Column(name = "profit_amount", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double profitAmount = 0.0;

    @Column(name = "tax_amount", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double taxAmount = 0.0;

    @Column(name = "final_amount", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double finalAmount = 0.0;

    private String notes;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransactionItem> items = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void calculateAmounts() {
        if (totalAmount != null && discountAmount != null) {
            finalAmount = totalAmount - discountAmount + (taxAmount != null ? taxAmount : 0.0);
        }
        if (totalAmount != null && totalCost != null) {
            profitAmount = totalAmount - totalCost;
        }
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }
}