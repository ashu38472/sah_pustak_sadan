package ashu.sah.SahPustakSadan.Repository;

import ashu.sah.SahPustakSadan.Model.Invoice;
import ashu.sah.SahPustakSadan.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByTransactions_Customer_Id(Long customerId);

    List<Invoice> findByIssuedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Invoice> findByStatus(InvoiceStatus status);
}
