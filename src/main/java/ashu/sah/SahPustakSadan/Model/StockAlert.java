package ashu.sah.SahPustakSadan.Model;

import ashu.sah.SahPustakSadan.enums.AlertType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_alerts", indexes = {
        @Index(name = "idx_stock_alert_product", columnList = "product_id"),
        @Index(name = "idx_stock_alert_type", columnList = "alert_type"),
        @Index(name = "idx_stock_alert_resolved", columnList = "is_resolved")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class StockAlert extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Column(nullable = false)
    private String message;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;

    @Column(name = "threshold_value", nullable = false)
    private Integer thresholdValue;

    @Column(name = "is_resolved", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isResolved = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private User resolvedBy;
}