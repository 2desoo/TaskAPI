package com.dubrovsky.task.restful.repository;

import com.dubrovsky.task.restful.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
