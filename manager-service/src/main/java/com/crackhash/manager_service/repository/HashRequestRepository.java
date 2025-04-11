package com.crackhash.manager_service.repository;

import com.crackhash.common.model.Status;
import com.crackhash.manager_service.model.HashRequestFullInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HashRequestRepository extends MongoRepository<HashRequestFullInfo, String> {
    List<HashRequestFullInfo> findByStatus(Status status);
}

