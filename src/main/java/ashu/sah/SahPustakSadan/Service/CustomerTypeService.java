package ashu.sah.SahPustakSadan.Service;

import ashu.sah.SahPustakSadan.Model.CustomerType;
import ashu.sah.SahPustakSadan.Repository.CustomerTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerTypeService {

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    public boolean createCustomerType(String name, String description, Double discountPercentage, Double creditLimit) {
        if (customerTypeRepository.existsByName(name)) {
            return false; // Customer type already exists
        }

        CustomerType customerType = new CustomerType();
        customerType.setName(name);
        customerType.setDescription(description);
        customerType.setDefaultDiscountPercentage(discountPercentage);
        customerType.setDefaultCreditLimit(creditLimit);
        customerType.setIsActive(true);

        customerTypeRepository.save(customerType);
        return true;
    }

    public boolean updateCustomerType(Long id, String name, String description, Double discountPercentage, Double creditLimit) {
        Optional<CustomerType> customerTypeOpt = customerTypeRepository.findById(id);
        if (customerTypeOpt.isPresent()) {
            CustomerType customerType = customerTypeOpt.get();

            // Check if name is being changed and if new name already exists
            if (!customerType.getName().equals(name) && customerTypeRepository.existsByName(name)) {
                return false;
            }

            customerType.setName(name);
            customerType.setDescription(description);
            customerType.setDefaultDiscountPercentage(discountPercentage);
            customerType.setDefaultCreditLimit(creditLimit);
            customerTypeRepository.save(customerType);
            return true;
        }
        return false;
    }

    public boolean deactivateCustomerType(Long id) {
        Optional<CustomerType> customerTypeOpt = customerTypeRepository.findById(id);
        if (customerTypeOpt.isPresent()) {
            CustomerType customerType = customerTypeOpt.get();
            customerType.setIsActive(false);
            customerTypeRepository.save(customerType);
            return true;
        }
        return false;
    }

    public CustomerType getByName(String name) {
        return customerTypeRepository.findByName(name).orElse(null);
    }

    public CustomerType getById(Long id) {
        return customerTypeRepository.findById(id).orElse(null);
    }

    public List<CustomerType> getAllActiveCustomerTypes() {
        return customerTypeRepository.findByIsActiveTrue();
    }

    public List<CustomerType> getAllCustomerTypes() {
        return customerTypeRepository.findAll();
    }

    public List<CustomerType> getCustomerTypesWithDiscount() {
        return customerTypeRepository.findByDefaultDiscountPercentageGreaterThan(0.0);
    }
}