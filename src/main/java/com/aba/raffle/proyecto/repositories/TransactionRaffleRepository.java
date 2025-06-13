package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.model.documents.TransactionRaffle;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRaffleRepository  extends MongoRepository<TransactionRaffle, ObjectId> {

}
