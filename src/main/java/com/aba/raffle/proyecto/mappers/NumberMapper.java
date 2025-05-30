package com.aba.raffle.proyecto.mappers;

import com.aba.raffle.proyecto.dto.NumberDTO;
import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NumberMapper {

    @Mapping(target = "raffleId", expression = "java(numberRaffle.getRaffleId().toHexString())")
    NumberDTO toNumberDTO(NumberRaffle numberRaffle); //convierte Number a NumberDTO

}
