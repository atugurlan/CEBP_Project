package com.backend.repository;

import com.backend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findOneById(Integer id);
    Client findOneByEmail(String email);
    Client findOneByName(String name);
}