package com.aba.raffle.proyecto.mappers;

import com.aba.raffle.proyecto.dto.TransactionRaffleDTO;
import com.aba.raffle.proyecto.model.entities.TransactionRaffle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionRaffleMapper {

    TransactionRaffleDTO toTransactionRaffleDTO(TransactionRaffle transactionRaffle);

    @Mapping(target = "id", ignore = true) // ID se genera automáticamente en PostgreSQL
    TransactionRaffle toTransactionRaffle(TransactionRaffleDTO transactionRaffleDTO);
}
