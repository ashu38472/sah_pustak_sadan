package ashu.sah.SahPustakSadan.Model;

import ashu.sah.SahPustakSadan.enums.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "inventory_logs", indexes = {
        @Index(name = "idx_inventory_log_product", columnList = "product_id"),
        @Index(name = "idx_inventory_log_movement_type", columnList = "movement_type"),
        @Index(name = "idx_inventory_log_date", columnList = "logged_at"),
        @Index(name = "idx_inventory_log_reference", columnList = "reference_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class InventoryLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Quantity change is required")
    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Column(name = "reference_id")
    private Long referenceId;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logged_by", nullable = false)
    private User loggedBy;

    @Column(name = "quantity_before", nullable = false)
    private Integer quantityBefore;

    @Column(name = "quantity_after", nullable = false)
    private Integer quantityAfter;
}