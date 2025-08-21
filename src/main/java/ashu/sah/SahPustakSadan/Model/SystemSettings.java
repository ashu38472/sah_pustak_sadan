package ashu.sah.SahPustakSadan.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "system_settings", indexes = {
        @Index(name = "idx_setting_key", columnList = "setting_key")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemSettings extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Setting key is required")
    @Column(name = "setting_key", unique = true, nullable = false)
    private String settingKey;

    @NotBlank(message = "Setting value is required")
    @Column(name = "setting_value", nullable = false)
    private String settingValue;

    @Column(name = "setting_type", nullable = false)
    private String settingType; // STRING, INTEGER, BOOLEAN, DECIMAL

    private String description;

    @Column(name = "is_editable", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isEditable = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}