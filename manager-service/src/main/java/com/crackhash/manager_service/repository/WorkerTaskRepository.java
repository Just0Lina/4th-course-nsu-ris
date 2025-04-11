package com.crackhash.manager_service.repository;

import com.crackhash.common.model.Status;
import com.crackhash.common.model.TaskStatus;
import com.crackhash.common.model.WorkerTask;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerTaskRepository extends MongoRepository<WorkerTask, String> {
    List<WorkerTask> findByStatus(TaskStatus taskStatus);
    
    Optional<WorkerTask>  findByRequestIdAndPartNumber(String requestId, int partNumber);

    void deleteByRequestIdAndPartNumber(String requestId, int partNumber);
}
