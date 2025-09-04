package ashu.sah.SahPustakSadan.Service;

import ashu.sah.SahPustakSadan.Model.Supplier;
import ashu.sah.SahPustakSadan.Repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public boolean createSupplier(String name, String phone, String email, String address) {
        if (supplierRepository.existsByPhone(phone) || supplierRepository.existsByEmail(email)) {
            return false; // Supplier already exists
        }

        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setPhone(phone);
        supplier.setEmail(email);
        supplier.setAddress(address);
        supplier.setIsActive(true);

        supplierRepository.save(supplier);
        return true;
    }

    public boolean updateSupplier(Long id, String name, String phone, String email, String address) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(id);
        if (supplierOpt.isPresent()) {
            Supplier supplier = supplierOpt.get();

            // Check uniqueness for phone
            if (!supplier.getPhone().equals(phone) && supplierRepository.existsByPhone(phone)) {
                return false;
            }

            // Check uniqueness for email
            if (!supplier.getEmail().equals(email) && supplierRepository.existsByEmail(email)) {
                return false;
            }

            supplier.setName(name);
            supplier.setPhone(phone);
            supplier.setEmail(email);
            supplier.setAddress(address);

            supplierRepository.save(supplier);
            return true;
        }
        return false;
    }

    public boolean deactivateSupplier(Long id) {
        Optional<Supplier> supplierOpt = supplierRepository.findById(id);
        if (supplierOpt.isPresent()) {
            Supplier supplier = supplierOpt.get();
            supplier.setIsActive(false);
            supplierRepository.save(supplier);
            return true;
        }
        return false;
    }

    public Supplier getByPhone(String phone) {
        return supplierRepository.findByPhone(phone).orElse(null);
    }

    public Supplier getByEmail(String email) {
        return supplierRepository.findByEmail(email).orElse(null);
    }

    public Supplier getById(Long id) {
        return supplierRepository.findById(id).orElse(null);
    }

    public List<Supplier> getAllActiveSuppliers() {
        return supplierRepository.findByIsActiveTrue();
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public List<Supplier> searchSuppliersByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name);
    }
}
