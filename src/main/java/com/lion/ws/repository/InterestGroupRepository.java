package com.lion.ws.repository;

import com.lion.ws.entity.InterestGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestGroupRepository extends JpaRepository<InterestGroup, Long> {
    List<InterestGroup> findByOwner(String owner);

    InterestGroup findByOwnerAndName(String owner, String name);
}
