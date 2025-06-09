package com.aba.raffle.proyecto.mappers;

import com.aba.raffle.proyecto.dto.RaffleCreateDTO;
import com.aba.raffle.proyecto.model.documents.Raffle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RaffleMapper {

    RaffleCreateDTO toRaffleDTO(Raffle raffle); //convierte Raffle a RaffleCreateDTO

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startdate", expression = "java(java.time.LocalDate.now().toString())")
    @Mapping(target = "stateRaffle", constant = "PAUSA")
    Raffle fromCreateRaffleDTO(RaffleCreateDTO raffleCreateDTO); //convierte RaffleCreateDTO a Raffle

}
