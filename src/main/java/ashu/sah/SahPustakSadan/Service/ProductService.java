package ashu.sah.SahPustakSadan.Service;

import ashu.sah.SahPustakSadan.Model.Category;
import ashu.sah.SahPustakSadan.Model.Product;
import ashu.sah.SahPustakSadan.Repository.CategoryRepository;
import ashu.sah.SahPustakSadan.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public boolean createProduct(String code, String name, String description,
                                 Double basePrice, Double costPrice, Long categoryId,
                                 String unit, String barcode) {
        if (productRepository.existsByCode(code)) {
            return false; // Product with this code already exists
        }

        Product product = new Product();
        product.setCode(code);
        product.setName(name);
        product.setDescription(description);
        product.setBasePrice(basePrice);
        product.setCostPrice(costPrice);
        product.setUnit(unit);
        product.setBarcode(barcode);

        if (categoryId != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            categoryOpt.ifPresent(product::setCategory);
        }

        product.setIsActive(true);
        productRepository.save(product);
        return true;
    }

    public boolean updateProduct(Long id, String code, String name,
                                 String description, Double basePrice, Double costPrice,
                                 Long categoryId, String unit, String barcode) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            // Ensure product code uniqueness
            if (!product.getCode().equals(code) && productRepository.existsByCode(code)) {
                return false;
            }

            product.setCode(code);
            product.setName(name);
            product.setDescription(description);
            product.setBasePrice(basePrice);
            product.setCostPrice(costPrice);
            product.setUnit(unit);
            product.setBarcode(barcode);

            if (categoryId != null) {
                Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
                categoryOpt.ifPresent(product::setCategory);
            }

            productRepository.save(product);
            return true;
        }
        return false;
    }

    public boolean deactivateProduct(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setIsActive(false);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    public Product getByCode(String code) {
        return productRepository.findByCode(code).orElse(null);
    }

    public Product getById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
