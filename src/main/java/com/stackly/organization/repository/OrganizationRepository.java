package com.stackly.organization.repository;

import com.stackly.organization.model.Organization;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrganizationRepository {

    private final Map<String, Organization> store = new ConcurrentHashMap<>();

    public Organization save(Organization organization) {
        store.put(organization.getOrganizationCode(), organization);
        return organization;
    }

    /** Atomically saves only if the code is not taken. Returns false if it already exists. */
    public boolean saveIfAbsent(Organization organization) {
        return store.putIfAbsent(organization.getOrganizationCode(), organization) == null;
    }

    public Optional<Organization> findByCode(String organizationCode) {
        return Optional.ofNullable(store.get(organizationCode));
    }

    public boolean existsByCode(String organizationCode) {
        return store.containsKey(organizationCode);
    }

    public Collection<Organization> findAll() {
        return store.values();
    }
}
