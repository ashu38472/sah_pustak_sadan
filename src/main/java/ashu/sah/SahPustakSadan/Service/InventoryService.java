package ashu.sah.SahPustakSadan.Service;

import ashu.sah.SahPustakSadan.Model.Inventory;
import ashu.sah.SahPustakSadan.Model.Product;
import ashu.sah.SahPustakSadan.Repository.InventoryRepository;
import ashu.sah.SahPustakSadan.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public boolean addOrUpdateInventory(Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);
            Inventory inventory;
            if (inventoryOpt.isPresent()) {
                inventory = inventoryOpt.get();
                inventory.setQuantity(inventory.getQuantity() + quantity);
            } else {
                inventory = new Inventory();
                inventory.setProduct(product);
                inventory.setQuantity(quantity);
            }
            inventoryRepository.save(inventory);
            return true;
        }
        return false;
    }

    public boolean reduceInventory(Long productId, Integer quantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            if (inventory.getQuantity() >= quantity) {
                inventory.setQuantity(inventory.getQuantity() - quantity);
                inventoryRepository.save(inventory);
                return true;
            }
        }
        return false;
    }

    public Inventory getByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId).orElse(null);
    }

    public List<Inventory> getLowStockItems(Integer threshold) {
        return inventoryRepository.findByQuantityLessThan(threshold);
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }
}
