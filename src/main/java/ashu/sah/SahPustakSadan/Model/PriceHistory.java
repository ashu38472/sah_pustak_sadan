package ashu.sah.SahPustakSadan.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "price_history", indexes = {
        @Index(name = "idx_price_history_product", columnList = "product_id"),
        @Index(name = "idx_price_history_customer_type", columnList = "customer_type_id"),
        @Index(name = "idx_price_history_date", columnList = "changed_at")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class PriceHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_type_id", nullable = false)
    private CustomerType customerType;

    @Column(name = "old_price", columnDefinition = "DECIMAL(10,2)")
    private Double oldPrice;

    @Column(name = "new_price", columnDefinition = "DECIMAL(10,2)")
    private Double newPrice;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    private String reason;
}