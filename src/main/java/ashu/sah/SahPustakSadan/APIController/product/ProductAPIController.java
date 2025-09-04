package ashu.sah.SahPustakSadan.APIController.product;

import ashu.sah.SahPustakSadan.Model.Product;
import ashu.sah.SahPustakSadan.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProductAPIController {

    @Autowired
    private ProductService productService;

    // --- Utility: Convert Product -> JSON-like Map ---
    private Map<String, Object> toJson(Product product) {
        if (product == null) return null;

        Map<String, Object> json = new HashMap<>();
        json.put("id", product.getId());
        json.put("code", product.getCode());
        json.put("name", product.getName());
        json.put("description", product.getDescription());
        json.put("barcode", product.getBarcode());
        json.put("unit", product.getUnit());
        json.put("basePrice", product.getBasePrice());
        json.put("costPrice", product.getCostPrice());
        json.put("isActive", product.getIsActive());

        if (product.getCategory() != null) {
            json.put("categoryId", product.getCategory().getId());
            json.put("categoryName", product.getCategory().getName());
        }

        return json;
    }

    private List<Map<String, Object>> toJsonList(List<Product> products) {
        if (products == null) return Collections.emptyList();
        return products.stream().map(this::toJson).collect(Collectors.toList());
    }

    // --- API-like methods (no @RestController) ---

    /** Create product */
    public boolean createProduct(String code, String name, String description,
                                 Double basePrice, Double costPrice, Long categoryId,
                                 String unit, String barcode) {
        return productService.createProduct(code, name, description, basePrice, costPrice, categoryId, unit, barcode);
    }

    /** Update product */
    public boolean updateProduct(Long id, String code, String name,
                                 String description, Double basePrice, Double costPrice,
                                 Long categoryId, String unit, String barcode) {
        return productService.updateProduct(id, code, name, description, basePrice, costPrice, categoryId, unit, barcode);
    }

    /** Deactivate product */
    public boolean deactivateProduct(Long id) {
        return productService.deactivateProduct(id);
    }

    /** Get product by ID */
    public Map<String, Object> getProductById(Long id) {
        return toJson(productService.getById(id));
    }

    /** Get product by Code */
    public Map<String, Object> getProductByCode(String code) {
        return toJson(productService.getByCode(code));
    }

    /** Get all active products */
    public List<Map<String, Object>> getActiveProducts() {
        return toJsonList(productService.getAllActiveProducts());
    }

    /** Get all products */
    public List<Map<String, Object>> getProducts() {
        return toJsonList(productService.getAllProducts());
    }

    /** Search products by name */
    public List<Map<String, Object>> searchProducts(String name) {
        return toJsonList(productService.searchProductsByName(name));
    }

    /** Soft delete product */
    public boolean deleteProduct(Long id) {
        return productService.deactivateProduct(id); // Marks as inactive
    }

}
