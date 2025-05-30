package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NumberRepository extends MongoRepository<NumberRaffle, ObjectId>, NumberRepositoryCustom{
    List<Number> findByBuyerEmailAndRaffleId(String email, String raffleId);
    List<NumberRaffle> findByBuyerEmail(String email);

}
