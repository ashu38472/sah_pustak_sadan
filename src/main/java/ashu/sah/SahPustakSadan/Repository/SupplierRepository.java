package ashu.sah.SahPustakSadan.Repository;

import ashu.sah.SahPustakSadan.Model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByPhone(String phone);

    Optional<Supplier> findByEmail(String email);

    List<Supplier> findByIsActiveTrue();

    List<Supplier> findByNameContainingIgnoreCase(String name);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);
}
