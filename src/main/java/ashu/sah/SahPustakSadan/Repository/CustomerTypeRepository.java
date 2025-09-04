package ashu.sah.SahPustakSadan.Repository;

import ashu.sah.SahPustakSadan.Model.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerTypeRepository extends JpaRepository<CustomerType, Long> {
    Optional<CustomerType> findByName(String name);

    List<CustomerType> findByIsActiveTrue();

    List<CustomerType> findByDefaultDiscountPercentageGreaterThan(Double percentage);

    boolean existsByName(String name);
}