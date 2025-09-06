package ashu.sah.SahPustakSadan.Front_end.Types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String code;
    private String name;
    private Long categoryId;
    private String categoryName;
    private String unit;
    private Double basePrice;
    private Double costPrice;
    private Integer currentStock;
    private Integer minStockLevel;
    private Boolean isActive;
    private String description;
    private String barcode;
    private Double taxPercentage;
    private String createdAt;
    private String updatedAt;
}
