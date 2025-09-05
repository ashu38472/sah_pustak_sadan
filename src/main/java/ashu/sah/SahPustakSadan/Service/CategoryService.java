package ashu.sah.SahPustakSadan.Service;

import ashu.sah.SahPustakSadan.Model.Category;
import ashu.sah.SahPustakSadan.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /** Create a new category */
    public boolean createCategory(String name, String description) {
        try {
            if (existsByName(name)) {
                return false; // Category with this name already exists
            }

            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            category.setIsActive(true);

            categoryRepository.save(category);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Update an existing category */
    public boolean updateCategory(Long id, String name, String description) {
        try {
            Optional<Category> categoryOpt = categoryRepository.findById(id);
            if (categoryOpt.isEmpty()) {
                return false;
            }

            Category category = categoryOpt.get();

            // Check if name is being changed and if new name already exists
            if (!category.getName().equals(name) && existsByName(name)) {
                return false;
            }

            category.setName(name);
            category.setDescription(description);

            categoryRepository.save(category);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Deactivate a category (soft delete) */
    public boolean deactivateCategory(Long id) {
        try {
            Optional<Category> categoryOpt = categoryRepository.findById(id);
            if (categoryOpt.isEmpty()) {
                return false;
            }

            Category category = categoryOpt.get();
            category.setIsActive(false);

            categoryRepository.save(category);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Get category by ID */
    public Category getById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    /** Get category by name */
    public Category getByName(String name) {
        return categoryRepository.findByName(name).orElse(null);
    }

    /** Get all active categories */
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    /** Get all categories (including inactive) */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /** Search categories by name */
    public List<Category> searchCategoriesByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }

    /** Check if category exists by name */
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}