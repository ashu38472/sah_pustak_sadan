package ashu.sah.SahPustakSadan.Repository;

import ashu.sah.SahPustakSadan.Model.Inventory;
import ashu.sah.SahPustakSadan.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);

    Optional<Inventory> findByProduct(Product product);

    List<Inventory> findByQuantityLessThan(Integer quantity);

    List<Inventory> findByQuantityGreaterThan(Integer quantity);

    // Products below their defined minimum stock level
    List<Inventory> findByQuantityLessThanEqualAndProduct_MinStockLevel(Integer quantity, Integer minStockLevel);
}
