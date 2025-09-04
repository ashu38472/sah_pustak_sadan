package ashu.sah.SahPustakSadan.Service;

import ashu.sah.SahPustakSadan.Model.Customer;
import ashu.sah.SahPustakSadan.Model.CustomerType;
import ashu.sah.SahPustakSadan.Repository.CustomerRepository;
import ashu.sah.SahPustakSadan.Repository.CustomerTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    public boolean createCustomer(String name, String phone, String email,
                                  String address, Long customerTypeId) {
        if (customerRepository.existsByPhone(phone) || customerRepository.existsByEmail(email)) {
            return false; // Customer with phone or email already exists
        }

        Customer customer = new Customer();
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAddress(address);

        if (customerTypeId != null) {
            Optional<CustomerType> customerTypeOpt = customerTypeRepository.findById(customerTypeId);
            customerTypeOpt.ifPresent(customer::setType);
        }

        customer.setIsActive(true);
        customerRepository.save(customer);
        return true;
    }

    public boolean updateCustomer(Long id, String name, String phone, String email,
                                  String address, Long customerTypeId) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();

            // Check uniqueness for phone
            if (!customer.getPhone().equals(phone) && customerRepository.existsByPhone(phone)) {
                return false;
            }

            // Check uniqueness for email
            if (!customer.getEmail().equals(email) && customerRepository.existsByEmail(email)) {
                return false;
            }

            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setAddress(address);

            if (customerTypeId != null) {
                Optional<CustomerType> customerTypeOpt = customerTypeRepository.findById(customerTypeId);
                customerTypeOpt.ifPresent(customer::setType);
            }

            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    public boolean deactivateCustomer(Long id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setIsActive(false);
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    public Customer getByPhone(String phone) {
        return customerRepository.findByPhone(phone).orElse(null);
    }

    public Customer getByEmail(String email) {
        return customerRepository.findByEmail(email).orElse(null);
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public List<Customer> getAllActiveCustomers() {
        return customerRepository.findByIsActiveTrue();
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> searchCustomersByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }
}
