package com.crackhash.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerResult  implements Serializable {
    private String requestId;
    private List<String> data;
}
