package com.aba.raffle.proyecto.mappers;

import com.aba.raffle.proyecto.dto.PaymentOperationDTO;
import com.aba.raffle.proyecto.model.documents.PaymentOperation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentOperationMapper {

    PaymentOperationDTO toDto(PaymentOperation entity);
    List<PaymentOperationDTO> toDtoList(List<PaymentOperation> entities);
}
