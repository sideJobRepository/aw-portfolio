package io.awportfoiioapi.security.service.impl;

import io.awportfoiioapi.security.service.PortfolioDynamicAuthorizationService;
import io.awportfoiioapi.security.service.PortfolioUrlRoleMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PortfolioDynamicAuthorizationServiceImpl implements PortfolioDynamicAuthorizationService {
    
    private final PortfolioUrlRoleMappingService roleMappingService;
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getUrlRoleMappings() {
        return roleMappingService.getRoleMappings();
    }
}
