package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.model.entities.PaymentOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentOperationRepository extends JpaRepository<PaymentOperation, Long> {

    List<PaymentOperation> findByRaffleId(String raffleId);

}
