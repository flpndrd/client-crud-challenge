package com.devsuperior.clientcrud.client.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.clientcrud.client.dto.ClientDTO;
import com.devsuperior.clientcrud.client.service.ClientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clients")
public class ClientController {

	private final ClientService clientService;

	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}

	@GetMapping("/{clientId}")
	ResponseEntity<ClientDTO> find(@PathVariable Long clientId) {
		ClientDTO clientDTO = clientService.find(clientId);
		return ResponseEntity.ok(clientDTO);
	}

	@GetMapping
	ResponseEntity<Page<ClientDTO>> list(Pageable pageable) {
		Page<ClientDTO> clientPage = clientService.list(pageable);
		return ResponseEntity.ok(clientPage);
	}

	@PostMapping
	ResponseEntity<ClientDTO> create(@Valid @RequestBody ClientDTO clientDTO) {
		ClientDTO createdClientDTO = clientService.create(clientDTO);
		URI uri = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{clientId}")
				.buildAndExpand(createdClientDTO.clientId())
				.toUri();

		return ResponseEntity.created(uri).body(createdClientDTO);
	}

	@PutMapping("/{clientId}")
	ResponseEntity<ClientDTO> update(@PathVariable Long clientId, @Valid @RequestBody ClientDTO clientDTO) {
		ClientDTO updatedClientDTO = clientService.update(clientId, clientDTO);
		return ResponseEntity.ok(updatedClientDTO);
	}

	@DeleteMapping("/{clientId}")
	ResponseEntity<Void> delete(@PathVariable Long clientId) {
		clientService.delete(clientId);
		return ResponseEntity.noContent().build();
	}

}
