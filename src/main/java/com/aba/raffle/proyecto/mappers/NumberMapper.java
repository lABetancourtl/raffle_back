package com.aba.raffle.proyecto.mappers;

import com.aba.raffle.proyecto.dto.NumberDTO;
import com.aba.raffle.proyecto.model.entities.NumberRaffle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NumberMapper {

    @Mapping(target = "raffleId", expression = "java(String.valueOf(numberRaffle.getRaffleId()))")
    NumberDTO toNumberDTO(NumberRaffle numberRaffle); // Convierte NumberRaffle a NumberDTO
}
