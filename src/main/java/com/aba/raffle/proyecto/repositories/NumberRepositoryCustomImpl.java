package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.model.entities.NumberRaffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NumberRepositoryCustomImpl implements NumberRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<NumberRaffle> findRandomAvailableNumbers(Long raffleId, EstadoNumber estado, int quantity) {
        String jpql = "SELECT n FROM NumberRaffle n " +
                "WHERE n.stateNumber = :estado AND n.raffleId = :raffleId " +
                "ORDER BY function('RANDOM')";

        TypedQuery<NumberRaffle> query = entityManager.createQuery(jpql, NumberRaffle.class);
        query.setParameter("estado", estado);
        query.setParameter("raffleId", raffleId);
        query.setMaxResults(quantity);

        return query.getResultList();
    }
}
