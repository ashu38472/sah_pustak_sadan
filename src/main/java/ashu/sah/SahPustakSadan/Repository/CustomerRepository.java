package ashu.sah.SahPustakSadan.Repository;

import ashu.sah.SahPustakSadan.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByEmail(String email);

    List<Customer> findByIsActiveTrue();

    List<Customer> findByNameContainingIgnoreCase(String name);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);
}
