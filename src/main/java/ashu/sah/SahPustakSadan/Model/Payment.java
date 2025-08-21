package ashu.sah.SahPustakSadan.Model;

import ashu.sah.SahPustakSadan.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_invoice", columnList = "invoice_id"),
        @Index(name = "idx_payment_method", columnList = "method"),
        @Index(name = "idx_payment_date", columnList = "payment_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "reference_number")
    private String referenceNumber;

    private String notes;

    @PrePersist
    private void setDefaultPaymentDate() {
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
    }
}