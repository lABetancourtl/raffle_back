package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import org.bson.types.ObjectId;

import java.util.List;

public interface NumberRepositoryCustom {
    List<NumberRaffle> findRandomAvailableNumbers(ObjectId raffleId, EstadoNumber estado, int quantity);
}
