package ashu.sah.SahPustakSadan.Front_end.Mapper;

import ashu.sah.SahPustakSadan.Front_end.Types.ProductDTO;
import ashu.sah.SahPustakSadan.Model.Product;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility to convert between Product entity and ProductDTO
 * This will help when transitioning to a separate backend
 */
@Component
public class ProductMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Convert Product entity to ProductDTO
     */
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setBarcode(product.getBarcode());
        dto.setUnit(product.getUnit());
        dto.setBasePrice(product.getBasePrice());
        dto.setCostPrice(product.getCostPrice());
        dto.setMinStockLevel(product.getMinStockLevel());
        dto.setIsActive(product.getIsActive());
        dto.setTaxPercentage(product.getTaxPercentage());

        // Handle category
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        // Handle timestamps
        if (product.getCreatedAt() != null) {
            dto.setCreatedAt(product.getCreatedAt().format(formatter));
        }
        if (product.getUpdatedAt() != null) {
            dto.setUpdatedAt(product.getUpdatedAt().format(formatter));
        }

        // TODO: Get current stock from inventory service
        // For now, set to 0 as placeholder
        dto.setCurrentStock(0);

        return dto;
    }

    /**
     * Convert ProductDTO to Product entity
     * Note: This won't include currentStock as it's not part of Product entity
     */
    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setId(dto.getId());
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setBarcode(dto.getBarcode());
        product.setUnit(dto.getUnit());
        product.setBasePrice(dto.getBasePrice());
        product.setCostPrice(dto.getCostPrice());
        product.setMinStockLevel(dto.getMinStockLevel());
        product.setIsActive(dto.getIsActive());
        product.setTaxPercentage(dto.getTaxPercentage());

        // Note: Category needs to be handled separately as it requires database lookup

        return product;
    }

    /**
     * Convert list of Product entities to list of ProductDTOs
     */
    public List<ProductDTO> toDTOList(List<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of ProductDTOs to list of Product entities
     */
    public List<Product> toEntityList(List<ProductDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update existing Product entity with data from ProductDTO
     * Useful for edit operations
     */
    public void updateEntity(Product product, ProductDTO dto) {
        if (product == null || dto == null) {
            return;
        }

        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setBarcode(dto.getBarcode());
        product.setUnit(dto.getUnit());
        product.setBasePrice(dto.getBasePrice());
        product.setCostPrice(dto.getCostPrice());
        product.setMinStockLevel(dto.getMinStockLevel());
        product.setIsActive(dto.getIsActive());
        product.setTaxPercentage(dto.getTaxPercentage());

        // Category update would require additional service call
    }
}