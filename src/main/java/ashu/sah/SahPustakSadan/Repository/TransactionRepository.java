package ashu.sah.SahPustakSadan.Repository;

import ashu.sah.SahPustakSadan.Model.Transaction;
import ashu.sah.SahPustakSadan.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomerId(Long customerId);

    List<Transaction> findByInvoice_Id(Long invoiceId);

    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByTransactionType(TransactionType type);
    List<Transaction> findByTransactionTypeAndCustomer_Id(TransactionType type, Long customerId);

}
