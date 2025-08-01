package com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

@XRayEnabled
public interface MongoUserRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByUsername(String username);
}