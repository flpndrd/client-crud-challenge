package com.devsuperior.clientcrud.client.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.clientcrud.client.dto.ClientDTO;
import com.devsuperior.clientcrud.client.entity.Client;
import com.devsuperior.clientcrud.client.exception.ClientNotFoundException;
import com.devsuperior.clientcrud.client.exception.CpfAlreadyRegisteredException;
import com.devsuperior.clientcrud.client.repository.ClientRepository;

@Service
public class ClientService {

	private final ClientRepository clientRepository;

	public ClientService(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	@Transactional(readOnly = true)
	public ClientDTO find(Long clientId) {
		Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException());
		return ClientDTO.from(client);
	}

	@Transactional(readOnly = true)
	public Page<ClientDTO> list(Pageable pageable) {
		Page<Client> clientPage = clientRepository.findAll(pageable);
		return clientPage.map(client -> ClientDTO.from(client));
	}

	@Transactional
	public ClientDTO create(ClientDTO clientDTO) {
		if (clientRepository.existsByCpf(clientDTO.cpf())) {
			throw new CpfAlreadyRegisteredException();
		}
		Client client = new Client(
				clientDTO.name(),
				clientDTO.cpf(),
				clientDTO.income(),
				clientDTO.birthDate(),
				clientDTO.children());

		Client savedClient = clientRepository.save(client);
		return ClientDTO.from(savedClient);
	}

	@Transactional
	public ClientDTO update(Long clientId, ClientDTO clientDTO) {
		Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException());

		if (clientRepository.existsByCpfAndClientIdNot(clientDTO.cpf(), clientId)) {
			throw new CpfAlreadyRegisteredException();
		}
		client.setName(clientDTO.name());
		client.setCpf(clientDTO.cpf());
		client.setIncome(clientDTO.income());
		client.setBirthDate(clientDTO.birthDate());
		client.setChildren(clientDTO.children());
		return ClientDTO.from(client);
	}

	@Transactional
	public void delete(Long clientId) {
		if (!clientRepository.existsById(clientId)) {
			throw new ClientNotFoundException();
		}
		clientRepository.deleteById(clientId);
	}

}
