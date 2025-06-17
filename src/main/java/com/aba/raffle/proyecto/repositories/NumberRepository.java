package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NumberRepository extends MongoRepository<NumberRaffle, ObjectId>, NumberRepositoryCustom{
    List<Number> findByBuyerEmailAndRaffleId(String email, String raffleId);
    List<NumberRaffle> findByBuyerEmail(String email);
    List<NumberRaffle> findByStateNumber(EstadoNumber estado);
    Optional<NumberRaffle> findByNumber(String number);
    List<NumberRaffle> findByStateNumberAndRaffleId(EstadoNumber estado, ObjectId raffleId);
    List<NumberRaffle> findByPaymentSessionId(String paymentSessionId);

}
