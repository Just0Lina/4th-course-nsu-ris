package com.crackhash.manager_service.service;

import com.crackhash.common.model.Status;
import com.crackhash.manager_service.model.HashRequestFullInfo;
import com.crackhash.manager_service.repository.HashRequestRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class WorkerTaskScheduler {
    private static final Logger logger = LoggerFactory.getLogger(WorkerTaskScheduler.class);

    private final HashRequestRepository hashRequestRepository;

    @Scheduled(fixedRate = 600000)
    public void checkWorkerResponses() {
        List<HashRequestFullInfo> ongoingRequests = hashRequestRepository.findByStatus(Status.IN_PROGRESS);

        for (HashRequestFullInfo request : ongoingRequests) {
            long timeElapsed = new Date().getTime() - request.getLastResponseTime().getTime();
            long timeLimit = 10 * 60 * 1000;

            if (timeElapsed > timeLimit && request.getWorkersRemaining() > 0) {
                request.setStatus(Status.ERROR);
                hashRequestRepository.save(request);
                logger.error("RequestId {} exceeded time limit, marking as ERROR.", request.getRequestId());
            }
        }
    }


}
