package ashu.sah.SahPustakSadan.Model;

import ashu.sah.SahPustakSadan.enums.InvoiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices", indexes = {
        @Index(name = "idx_invoice_transaction", columnList = "transaction_id"),
        @Index(name = "idx_invoice_number", columnList = "invoice_number"),
        @Index(name = "idx_invoice_status", columnList = "status"),
        @Index(name = "idx_invoice_due_date", columnList = "due_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class Invoice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @DecimalMin(value = "0.0", message = "Total amount cannot be negative")
    @Column(name = "total_amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double totalAmount;

    @DecimalMin(value = "0.0", message = "Paid amount cannot be negative")
    @Column(name = "paid_amount", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double paidAmount = 0.0;

    @Column(name = "balance_amount", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double balanceAmount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void calculateBalance() {
        if (totalAmount != null && paidAmount != null) {
            balanceAmount = totalAmount - paidAmount;
        }
        if (issuedAt == null) {
            issuedAt = LocalDateTime.now();
        }
    }
}