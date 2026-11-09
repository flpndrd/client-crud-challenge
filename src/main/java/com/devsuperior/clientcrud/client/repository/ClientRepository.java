package com.devsuperior.clientcrud.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsuperior.clientcrud.client.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

	boolean existsByCpf(String cpf);

	boolean existsByCpfAndClientIdNot(String cpf, Long clientId);

}
