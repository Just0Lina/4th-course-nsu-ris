package com.crackhash.manager_service.controller;

import com.crackhash.common.model.WorkerResult;
import com.crackhash.manager_service.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/api/manager")
@RequiredArgsConstructor
public class InternalController {
    private final ManagerService managerService;

    @PatchMapping("/hash/crack/request")
    public ResponseEntity<Void> receiveWorkerResult(@RequestBody WorkerResult result) {
        managerService.processWorkerResult(result);
        return ResponseEntity.ok().build();
    }
}
