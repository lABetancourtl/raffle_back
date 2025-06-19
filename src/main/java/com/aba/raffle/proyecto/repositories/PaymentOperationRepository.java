package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.mappers.RaffleMapper;
import com.aba.raffle.proyecto.model.documents.PaymentOperation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentOperationRepository extends MongoRepository<PaymentOperation, String> {
    List<PaymentOperation> findByRaffleId(String raffleId);
}
