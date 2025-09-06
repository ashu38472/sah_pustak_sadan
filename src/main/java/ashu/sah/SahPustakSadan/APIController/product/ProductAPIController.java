package ashu.sah.SahPustakSadan.APIController.product;

import ashu.sah.SahPustakSadan.Front_end.Types.ProductDTO;
import ashu.sah.SahPustakSadan.Model.Product;
import ashu.sah.SahPustakSadan.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductAPIController {

    @Autowired
    private ProductService productService;

    // --- Utility: Convert Product -> DTO ---
    private ProductDTO toDTO(Product product) {
        if (product == null) return null;

        return ProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .barcode(product.getBarcode())
                .unit(product.getUnit())
                .basePrice(product.getBasePrice())
                .costPrice(product.getCostPrice())
                .isActive(product.getIsActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }

    private List<ProductDTO> toDTOList(List<Product> products) {
        if (products == null) return Collections.emptyList();
        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // --- API-like methods (no @RestController) ---

    /** Create product */
    public boolean createProduct(ProductDTO productDTO) {
        return productService.createProduct(
                productDTO.getCode(),
                productDTO.getName(),
                productDTO.getDescription(),
                productDTO.getBasePrice(),
                productDTO.getCostPrice(),
                productDTO.getCategoryId(),
                productDTO.getUnit(),
                productDTO.getBarcode()
        );
    }

    /** Update product */
    public boolean updateProduct(Long id, ProductDTO productDTO) {
        return productService.updateProduct(
                id,
                productDTO.getCode(),
                productDTO.getName(),
                productDTO.getDescription(),
                productDTO.getBasePrice(),
                productDTO.getCostPrice(),
                productDTO.getCategoryId(),
                productDTO.getUnit(),
                productDTO.getBarcode()
        );
    }

    /** Deactivate product */
    public boolean deactivateProduct(Long id) {
        return productService.deactivateProduct(id);
    }

    /** Get product by ID */
    public ProductDTO getProductById(Long id) {
        return toDTO(productService.getById(id));
    }

    /** Get product by Code */
    public ProductDTO getProductByCode(String code) {
        return toDTO(productService.getByCode(code));
    }

    /** Get all active products */
    public List<ProductDTO> getActiveProducts() {
        return toDTOList(productService.getAllActiveProducts());
    }

    /** Get all products */
    public List<ProductDTO> getProducts() {
        return toDTOList(productService.getAllProducts());
    }

    /** Search products by name */
    public List<ProductDTO> searchProducts(String name) {
        return toDTOList(productService.searchProductsByName(name));
    }

    /** Soft delete product */
    public boolean deleteProduct(Long id) {
        return productService.deactivateProduct(id); // Marks as inactive
    }
}
