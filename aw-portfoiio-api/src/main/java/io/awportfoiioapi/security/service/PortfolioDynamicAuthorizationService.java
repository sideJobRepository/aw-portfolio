package io.awportfoiioapi.security.service;

import java.util.Map;

public interface PortfolioDynamicAuthorizationService {
    Map<String, String> getUrlRoleMappings();
}
