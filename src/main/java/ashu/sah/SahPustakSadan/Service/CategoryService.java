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

    public boolean createCategory(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            return false; // Category already exists
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIsActive(true);

        categoryRepository.save(category);
        return true;
    }

    public boolean updateCategory(Long id, String name, String description) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();

            // Check if name is being changed and if new name already exists
            if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
                return false;
            }

            category.setName(name);
            category.setDescription(description);
            categoryRepository.save(category);
            return true;
        }
        return false;
    }

    public boolean deactivateCategory(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            category.setIsActive(false);
            categoryRepository.save(category);
            return true;
        }
        return false;
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name).orElse(null);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> searchCategoriesByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }
}