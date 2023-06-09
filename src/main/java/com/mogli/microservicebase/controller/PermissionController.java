package com.mogli.microservicebase.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PermissionController {
    @GetMapping("/roles")
    public String getRolesOfUser() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
    }
}
