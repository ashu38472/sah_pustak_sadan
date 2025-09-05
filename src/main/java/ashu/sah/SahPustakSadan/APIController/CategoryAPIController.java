package ashu.sah.SahPustakSadan.APIController;

import ashu.sah.SahPustakSadan.Model.Category;
import ashu.sah.SahPustakSadan.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CategoryAPIController {

    @Autowired
    private CategoryService categoryService;

    // --- Utility: Convert Category -> JSON-like Map ---
    private Map<String, Object> toJson(Category category) {
        if (category == null) return null;

        Map<String, Object> json = new HashMap<>();
        json.put("id", category.getId());
        json.put("name", category.getName());
        json.put("description", category.getDescription());
        json.put("isActive", category.getIsActive());

        return json;
    }

    private List<Map<String, Object>> toJsonList(List<Category> categories) {
        if (categories == null) return Collections.emptyList();
        return categories.stream().map(this::toJson).collect(Collectors.toList());
    }

    // --- API-like methods (no @RestController) ---

    /** Create category */
    public boolean createCategory(String name, String description) {
        return categoryService.createCategory(name, description);
    }

    /** Update category */
    public boolean updateCategory(Long id, String name, String description) {
        return categoryService.updateCategory(id, name, description);
    }

    /** Deactivate category */
    public boolean deactivateCategory(Long id) {
        return categoryService.deactivateCategory(id);
    }

    /** Get category by ID */
    public Map<String, Object> getCategoryById(Long id) {
        return toJson(categoryService.getById(id));
    }

    /** Get category by name */
    public Map<String, Object> getCategoryByName(String name) {
        return toJson(categoryService.getByName(name));
    }

    /** Get all active categories */
    public List<Map<String, Object>> getActiveCategories() {
        return toJsonList(categoryService.getAllActiveCategories());
    }

    /** Get all categories */
    public List<Map<String, Object>> getCategories() {
        return toJsonList(categoryService.getAllCategories());
    }

    /** Search categories by name */
    public List<Map<String, Object>> searchCategories(String name) {
        return toJsonList(categoryService.searchCategoriesByName(name));
    }

    /** Soft delete category */
    public boolean deleteCategory(Long id) {
        return categoryService.deactivateCategory(id); // Marks as inactive
    }

    /** Check if category exists */
    public boolean categoryExists(String name) {
        return categoryService.existsByName(name);
    }
}