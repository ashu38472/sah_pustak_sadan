package ashu.sah.SahPustakSadan.Service;

import ashu.sah.SahPustakSadan.Model.*;
import ashu.sah.SahPustakSadan.enums.TransactionStatus;
import ashu.sah.SahPustakSadan.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * Create a new transaction with items
     */
    @Transactional
    public Transaction createTransaction(Transaction transaction, Long customerId, Long userId) {
        // Validate customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + customerId));

        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        transaction.setCustomer(customer);
        transaction.setUser(user);
        transaction.setStatus(TransactionStatus.PENDING);

        // Attach transaction to items & validate products
        for (TransactionItem item : transaction.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + item.getProduct().getId()));

            item.setTransaction(transaction);
            item.setProduct(product);
            item.setCostPrice(product.getCostPrice());
            item.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice() : product.getBasePrice());
        }

        // Persist transaction (calculations run via @PrePersist)
        return transactionRepository.save(transaction);
    }

    /**
     * Update transaction status
     */
    @Transactional
    public Transaction updateTransactionStatus(Long transactionId, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + transactionId));

        transaction.setStatus(status);

        // If transaction is completed, reduce stock
        if (status == TransactionStatus.COMPLETED) {
            for (TransactionItem item : transaction.getItems()) {
                Inventory inventory = inventoryRepository.findByProduct(item.getProduct())
                        .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product: " + item.getProduct().getId()));

                if (inventory.getAvailableQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for product: " + item.getProduct().getName());
                }

                inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
                inventoryRepository.save(inventory);
            }
        }

        return transactionRepository.save(transaction);
    }

    /**
     * Get transaction by ID
     */
    @Transactional(readOnly = true)
    public Transaction getTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + id));
    }

    /**
     * Get all transactions
     */
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Get transactions by customer
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCustomer(Long customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }

    /**
     * Soft delete transaction
     */
    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + id));

        transaction.markAsDeleted();
        transactionRepository.save(transaction);
    }
}
