package ashu.sah.SahPustakSadan.Repository;

import ashu.sah.SahPustakSadan.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);

    List<Product> findByIsActiveTrue();

    List<Product> findByNameContainingIgnoreCase(String name);

    boolean existsByCode(String code);
}
