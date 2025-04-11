package com.crackhash.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerTask  implements Serializable {
    private String requestId;
    private String hash;
    private int maxLength;
    private int partNumber;
    private int partCount;

    private TaskStatus status;

    public WorkerTask(String requestId, String hash, int maxLength, int partNumber, int partCount) {
        this.requestId = requestId;
        this.hash = hash;
        this.maxLength = maxLength;
        this.partNumber = partNumber;
        this.partCount = partCount;
    }
}
