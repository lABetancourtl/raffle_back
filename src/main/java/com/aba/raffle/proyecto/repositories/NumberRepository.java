package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.model.entities.NumberRaffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NumberRepository extends JpaRepository<NumberRaffle, String>, NumberRepositoryCustom {

    // Buscar por email y raffleId
    List<NumberRaffle> findByBuyerEmailAndRaffleId(String email, Long raffleId);

    List<NumberRaffle> findByBuyerEmail(String email);

    List<NumberRaffle> findByStateNumber(EstadoNumber estado);

    Optional<NumberRaffle> findByNumber(String number);

    List<NumberRaffle> findByStateNumberAndRaffleId(EstadoNumber estado, Long raffleId);

    List<NumberRaffle> findByPaymentSessionId(String paymentSessionId);

    List<NumberRaffle> findByStateNumberAndReservedAtBefore(EstadoNumber estadoNumber, LocalDateTime hace10Min);

    List<NumberRaffle> findByStateNumberAndRaffleIdIn(EstadoNumber stateNumber, List<Long> raffleIds);

    // Llamado para obtener un número disponible
    NumberRaffle findByNumberAndStateNumber(String number, EstadoNumber stateNumber);
}
