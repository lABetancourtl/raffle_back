package com.aba.raffle.proyecto.mappers;

import com.aba.raffle.proyecto.dto.NumeroDTO;
import com.aba.raffle.proyecto.dto.PaymentOperationDTO;
import com.aba.raffle.proyecto.model.entities.PaymentOperation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PaymentOperationMapper {

    @Mapping(source = "numerosComprados", target = "numerosComprados", qualifiedByName = "stringListToNumeroDTOList")
    PaymentOperationDTO toDto(PaymentOperation entity);

    List<PaymentOperationDTO> toDtoList(List<PaymentOperation> entities);

    // Método para mapear List<String> a List<NumeroDTO>
    @Named("stringListToNumeroDTOList")
    default List<NumeroDTO> map(List<String> fullNumbers) {
        if (fullNumbers == null) return null;
        return fullNumbers.stream()
                .map(numero -> {
                    String numeroExtraido = numero.substring(numero.lastIndexOf("_") + 1);
                    return new NumeroDTO(numeroExtraido);
                })
                .collect(Collectors.toList());
    }

    // Método para mapear un String individual a NumeroDTO
    default NumeroDTO stringToNumeroDTO(String numero) {
        if (numero == null) return null;
        String numeroExtraido = numero.substring(numero.lastIndexOf("_") + 1);
        return new NumeroDTO(numeroExtraido);
    }
}
