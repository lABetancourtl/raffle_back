package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.model.entities.TransactionRaffle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRaffleRepository extends JpaRepository<TransactionRaffle, Long> {

}
