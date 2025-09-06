package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.model.entities.UserAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAdminRepository extends JpaRepository<UserAdmin, Long> {

    Optional<UserAdmin> findByEmail(String email);

    Optional<UserAdmin> findById(Long id);
}
