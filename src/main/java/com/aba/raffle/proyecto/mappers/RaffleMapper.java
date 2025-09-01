package com.aba.raffle.proyecto.mappers;

import com.aba.raffle.proyecto.dto.RaffleCreateDTO;
import com.aba.raffle.proyecto.model.entities.Raffle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RaffleMapper {

    RaffleCreateDTO toRaffleDTO(Raffle raffle); // Convierte Raffle a RaffleCreateDTO

    @Mapping(target = "id", ignore = true) // ID generado automáticamente en PostgreSQL
    @Mapping(target = "startDate", expression = "java(java.time.LocalDate.now().toString())")
    @Mapping(target = "stateRaffle", constant = "PAUSA")
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "porcentajeVendidos", ignore = true)
    @Mapping(target = "cantidadDisponibles", ignore = true)
    Raffle fromCreateRaffleDTO(RaffleCreateDTO raffleCreateDTO); // Convierte RaffleCreateDTO a Raffle
}
