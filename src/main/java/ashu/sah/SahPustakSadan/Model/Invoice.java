package ashu.sah.SahPustakSadan.Model;

import ashu.sah.SahPustakSadan.enums.InvoiceStatus;
import ashu.sah.SahPustakSadan.enums.TransactionStatus;
import ashu.sah.SahPustakSadan.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices", indexes = {
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

    @OneToMany(mappedBy = "invoice", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

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
        // Default issuedAt
        if (issuedAt == null) {
            issuedAt = LocalDateTime.now();
        }

        // Check if any refund transaction exists
        boolean hasRefund = transactions.stream()
                .anyMatch(t -> t.getTransactionType() == TransactionType.REFUND
                        && t.getStatus() == TransactionStatus.COMPLETED);

        if (hasRefund) {
            // Full refund: reset paid amount
            paidAmount = 0.0;
            balanceAmount = totalAmount;
            status = InvoiceStatus.REFUNDED; // Optional: add a REFUNDED enum
        } else {
            // Sum of all completed payment transactions
            double sumPaid = transactions.stream()
                    .filter(t -> t.getTransactionType() == TransactionType.PAYMENT
                            && t.getStatus() == TransactionStatus.COMPLETED)
                    .mapToDouble(Transaction::getFinalAmount)
                    .sum();

            paidAmount = sumPaid;
            balanceAmount = totalAmount - paidAmount;

            // Update status based on payment
            if (balanceAmount <= 0) {
                status = InvoiceStatus.PAID;
            } else if (paidAmount > 0) {
                status = InvoiceStatus.PARTIALLY_PAID;
            } else {
                status = InvoiceStatus.UNPAID;
            }
        }
    }
}