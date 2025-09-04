package ashu.sah.SahPustakSadan.Service;

import ashu.sah.SahPustakSadan.Model.Customer;
import ashu.sah.SahPustakSadan.Model.Invoice;
import ashu.sah.SahPustakSadan.Model.Transaction;
import ashu.sah.SahPustakSadan.Repository.CustomerRepository;
import ashu.sah.SahPustakSadan.Repository.InvoiceRepository;
import ashu.sah.SahPustakSadan.Repository.TransactionRepository;
import ashu.sah.SahPustakSadan.enums.InvoiceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Create invoice from multiple transactions.
     */
    public boolean createInvoice(Long customerId, List<Long> transactionIds,
                                 Double paidAmount, LocalDateTime dueDate,
                                 InvoiceStatus status) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) return false;

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis()); // simple generator
        invoice.setIssuedAt(LocalDateTime.now());
        invoice.setDueDate(dueDate);
        invoice.setStatus(status);

        double totalAmount = 0.0;

        // Attach transactions
        for (Long transactionId : transactionIds) {
            Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
            if (transactionOpt.isPresent()) {
                Transaction transaction = transactionOpt.get();
                transaction.setInvoice(invoice); // link both ways
                invoice.getTransactions().add(transaction);
                totalAmount += transaction.getFinalAmount(); // use computed final amount
            }
        }

        invoice.setTotalAmount(totalAmount);
        invoice.setPaidAmount(paidAmount != null ? paidAmount : 0.0);

        invoiceRepository.save(invoice);
        return true;
    }

    /**
     * Update invoice status (e.g. PAID, PARTIAL, REFUNDED, CANCELLED).
     */
    public boolean updateInvoiceStatus(Long id, InvoiceStatus status) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(id);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            invoice.setStatus(status);
            invoiceRepository.save(invoice);
            return true;
        }
        return false;
    }

    public Invoice getById(Long id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    public List<Invoice> getByCustomerId(Long customerId) {
        return invoiceRepository.findByTransactions_Customer_Id(customerId);
    }

    public List<Invoice> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.findByIssuedAtBetween(startDate, endDate);
    }

    public List<Invoice> getByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(InvoiceStatus.valueOf(status.name()));
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
}

