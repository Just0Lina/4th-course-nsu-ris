package com.crackhash.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HashStatus {
    private Status status;
    private List<String> data;

    public HashStatus(HashStatus hashStatus) {
        status = hashStatus.status;
        data = hashStatus.data;
    }
}
