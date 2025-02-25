package com.crackhash.manager_service.controller;

import com.crackhash.common.model.HashRequest;
import com.crackhash.common.model.HashStatus;
import com.crackhash.manager_service.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/hash")
@RequiredArgsConstructor
public class ManagerController {
    private final ManagerService managerService;

    @PostMapping("/crack")
    public ResponseEntity<Map<String, String>> crackHash(@RequestBody HashRequest request) {
        String requestId = managerService.processRequest(request);
        return ResponseEntity.ok(Collections.singletonMap("requestId", requestId));
    }

    @GetMapping("/status")
    public ResponseEntity<HashStatus> getStatus(@RequestParam String requestId) {
        return ResponseEntity.ok(managerService.getRequestStatus(requestId));
    }
}
