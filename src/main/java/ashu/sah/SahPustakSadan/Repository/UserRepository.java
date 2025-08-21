package ashu.sah.SahPustakSadan.Repository;

import ashu.sah.SahPustakSadan.Model.User;
import ashu.sah.SahPustakSadan.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByFullName(String fullName);
    List<User> findByIsActiveTrue();
    List<User> findByRole(UserRole role);
    List<User> findByIsActiveTrueAndRole(UserRole role);
}
