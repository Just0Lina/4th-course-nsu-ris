package com.crackhash.manager_service.mapper;

import com.crackhash.common.model.Status;
import com.crackhash.common.model.WorkerResult;
import com.crackhash.manager_service.model.HashRequestFullInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;

@Mapper(componentModel = "spring")
public interface HashRequestMapper {

    default void updateHashRequestFullInfo(HashRequestFullInfo hashRequestFullInfo, WorkerResult result) {
        if (hashRequestFullInfo.getFoundWords() == null) {
            hashRequestFullInfo.setFoundWords(new ArrayList<>());
        }
        hashRequestFullInfo.getFoundWords().addAll(result.getData());
        hashRequestFullInfo.setStatus(Status.READY);
        hashRequestFullInfo.setWorkersRemaining(hashRequestFullInfo.getWorkersRemaining() - 1);
    }
}