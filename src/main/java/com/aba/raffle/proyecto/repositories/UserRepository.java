package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.model.entities.User;
import com.aba.raffle.proyecto.model.enums.EstadoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByEstadoUser(EstadoUser estadoUser);
}

