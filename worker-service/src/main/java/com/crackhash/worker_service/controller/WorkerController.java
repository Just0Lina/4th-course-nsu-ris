package com.crackhash.worker_service.controller;

import com.crackhash.common.model.WorkerTask;
import com.crackhash.worker_service.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/worker")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerService workerService;

    @PostMapping("/hash/crack/task")
    public ResponseEntity<Void> processTask(@RequestBody WorkerTask task) {
        workerService.crackHash(task);
        return ResponseEntity.ok().build();
    }
}
