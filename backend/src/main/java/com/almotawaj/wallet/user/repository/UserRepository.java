package com.almotawaj.wallet.user.repository;

import com.almotawaj.wallet.statistics.model.RoleCount;
import com.almotawaj.wallet.user.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    boolean existsByEmailAddress(String emailAddress);

    boolean existsByMobileNumber(String mobileNumber);

    Optional<User> findByEmailAddress(String emailAddress);

    Optional<User> findByMobileNumber(String mobileNumber);

    List<User> findAllByEmailAddressIn(Collection<String> emailAddresses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :id")
    Optional<User> findByIdForUpdate(UUID id);

    @Query("select new com.almotawaj.wallet.statistics.model.RoleCount(u.role, count(u)) from User u group by u.role")
    List<RoleCount> countByRole();
}
