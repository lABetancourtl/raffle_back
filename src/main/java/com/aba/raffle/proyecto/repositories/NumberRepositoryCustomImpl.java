package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.repositories.NumberRepositoryCustom;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NumberRepositoryCustomImpl implements NumberRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<NumberRaffle> findRandomAvailableNumbers(ObjectId raffleId, EstadoNumber estado, int quantity) {
        MatchOperation match = Aggregation.match(
                Criteria.where("raffleId").is(raffleId)
                        .and("stateNumber").is(estado)
        );

        SampleOperation sample = Aggregation.sample(quantity);

        Aggregation aggregation = Aggregation.newAggregation(match, sample);

        AggregationResults<NumberRaffle> results = mongoTemplate.aggregate(aggregation, "numbers", NumberRaffle.class);

        return results.getMappedResults();
    }
}
