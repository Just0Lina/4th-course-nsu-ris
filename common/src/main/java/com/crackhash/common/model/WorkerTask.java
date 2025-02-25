package com.crackhash.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerTask {
    private String requestId;
    private String hash;
    private int maxLength;
    private int partNumber;
    private int partCount;
}
