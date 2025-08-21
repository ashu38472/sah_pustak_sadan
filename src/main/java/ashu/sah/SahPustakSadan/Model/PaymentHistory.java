package ashu.sah.SahPustakSadan.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_history", indexes = {
        @Index(name = "idx_payment_history_customer", columnList = "customer_id"),
        @Index(name = "idx_payment_history_date", columnList = "payment_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    private String notes;

    @PrePersist
    private void setDefaultPaymentDate() {
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
    }
}