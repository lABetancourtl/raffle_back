package com.aba.raffle.proyecto.mappers;

import com.aba.raffle.proyecto.dto.BuyerDTO;
import com.aba.raffle.proyecto.dto.TransactionRaffleDTO;
import com.aba.raffle.proyecto.model.documents.TransactionRaffle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionRaffleMapper {

    TransactionRaffleDTO toTransactionRaffleDTO(TransactionRaffle transactionRaffle);

    @Mapping(target = "id", ignore = true)
    TransactionRaffle toTransactionRaffle(TransactionRaffleDTO transactionRaffleDTO);
}
