package com.crackhash.manager_service.model;

import com.crackhash.common.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
public class HashRequestFullInfo {
    @Id
    private String requestId;
    private String hash;
    private int maxLength;
    private Status status;
    private List<String> foundWords;
    private int workersRemaining;
    private Date lastResponseTime;

    public HashRequestFullInfo(String requestId, String hash, int maxLength, Status status,int workersRemaining) {
        this.requestId = requestId;
        this.hash = hash;
        this.maxLength = maxLength;
        this.status = status;
        this.workersRemaining = workersRemaining;
        this.lastResponseTime = new Date();
    }

    public HashRequestFullInfo() {
    }
}
