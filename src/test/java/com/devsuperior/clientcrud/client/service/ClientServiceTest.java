package com.devsuperior.clientcrud.client.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.devsuperior.clientcrud.client.dto.ClientDTO;
import com.devsuperior.clientcrud.client.entity.Client;
import com.devsuperior.clientcrud.client.exception.ClientNotFoundException;
import com.devsuperior.clientcrud.client.exception.CpfAlreadyRegisteredException;
import com.devsuperior.clientcrud.client.repository.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

	private static final Long EXISTING_CLIENT_ID = 1L;

	private static final Long NONEXISTENT_CLIENT_ID = 999L;

	@Mock
	private ClientRepository clientRepository;

	@InjectMocks
	private ClientService clientService;

	@Nested
	class Find {

		@Test
		void shouldReturnClientWhenClientExists() {
			given(clientRepository.findById(EXISTING_CLIENT_ID)).willReturn(Optional.of(anExistingClient()));

			ClientDTO clientDTO = clientService.find(EXISTING_CLIENT_ID);

			assertThat(clientDTO.clientId()).isEqualTo(EXISTING_CLIENT_ID);
			assertThat(clientDTO.name()).isEqualTo("Maria Silva");
		}

		@Test
		void shouldThrowClientNotFoundExceptionWhenClientDoesNotExist() {
			given(clientRepository.findById(NONEXISTENT_CLIENT_ID)).willReturn(Optional.empty());

			assertThatThrownBy(() -> clientService.find(NONEXISTENT_CLIENT_ID))
					.isInstanceOf(ClientNotFoundException.class);
		}

	}

	@Nested
	class ListClients {

		@Test
		void shouldReturnClientPage() {
			Pageable pageable = PageRequest.of(0, 6);
			given(clientRepository.findAll(pageable)).willReturn(new PageImpl<>(List.of(anExistingClient())));

			Page<ClientDTO> clientPage = clientService.list(pageable);

			assertThat(clientPage.getContent())
					.extracting(clientDTO -> clientDTO.name())
					.containsExactly("Maria Silva");
		}

	}

	@Nested
	class Create {

		@Test
		void shouldCreateClientWhenCpfIsNotRegistered() {
			ClientDTO clientDTO = aClientDTO("Maria Silva");
			given(clientRepository.existsByCpf(clientDTO.cpf())).willReturn(false);
			given(clientRepository.save(any(Client.class))).willReturn(anExistingClient());

			ClientDTO createdClientDTO = clientService.create(clientDTO);

			assertThat(createdClientDTO.clientId()).isEqualTo(EXISTING_CLIENT_ID);
			then(clientRepository).should().save(any(Client.class));
		}

		@Test
		void shouldThrowCpfAlreadyRegisteredExceptionWhenCpfIsRegistered() {
			ClientDTO clientDTO = aClientDTO("Maria Silva");
			given(clientRepository.existsByCpf(clientDTO.cpf())).willReturn(true);

			assertThatThrownBy(() -> clientService.create(clientDTO)).isInstanceOf(CpfAlreadyRegisteredException.class);
			then(clientRepository).should(never()).save(any(Client.class));
		}

	}

	@Nested
	class Update {

		@Test
		void shouldUpdateClientWhenClientExistsAndCpfIsNotRegisteredForAnotherClient() {
			ClientDTO clientDTO = aClientDTO("Maria Silvaaa");
			given(clientRepository.findById(EXISTING_CLIENT_ID)).willReturn(Optional.of(anExistingClient()));
			given(clientRepository.existsByCpfAndClientIdNot(clientDTO.cpf(), EXISTING_CLIENT_ID)).willReturn(false);

			ClientDTO updatedClientDTO = clientService.update(EXISTING_CLIENT_ID, clientDTO);

			assertThat(updatedClientDTO.clientId()).isEqualTo(EXISTING_CLIENT_ID);
			assertThat(updatedClientDTO.name()).isEqualTo("Maria Silvaaa");
		}

		@Test
		void shouldThrowClientNotFoundExceptionWhenClientDoesNotExist() {
			ClientDTO clientDTO = aClientDTO("Maria Silvaaa");
			given(clientRepository.findById(NONEXISTENT_CLIENT_ID)).willReturn(Optional.empty());

			assertThatThrownBy(() -> clientService.update(NONEXISTENT_CLIENT_ID, clientDTO))
					.isInstanceOf(ClientNotFoundException.class);
		}

		@Test
		void shouldThrowCpfAlreadyRegisteredExceptionWhenCpfIsRegisteredForAnotherClient() {
			ClientDTO clientDTO = aClientDTO("Maria Silvaaa");
			given(clientRepository.findById(EXISTING_CLIENT_ID)).willReturn(Optional.of(anExistingClient()));
			given(clientRepository.existsByCpfAndClientIdNot(clientDTO.cpf(), EXISTING_CLIENT_ID)).willReturn(true);

			assertThatThrownBy(() -> clientService.update(EXISTING_CLIENT_ID, clientDTO))
					.isInstanceOf(CpfAlreadyRegisteredException.class);
		}

	}

	@Nested
	class Delete {

		@Test
		void shouldDeleteClientWhenClientExists() {
			given(clientRepository.existsById(EXISTING_CLIENT_ID)).willReturn(true);

			clientService.delete(EXISTING_CLIENT_ID);

			then(clientRepository).should().deleteById(EXISTING_CLIENT_ID);
		}

		@Test
		void shouldThrowClientNotFoundExceptionWhenClientDoesNotExist() {
			given(clientRepository.existsById(NONEXISTENT_CLIENT_ID)).willReturn(false);

			assertThatThrownBy(() -> clientService.delete(NONEXISTENT_CLIENT_ID))
					.isInstanceOf(ClientNotFoundException.class);
			then(clientRepository).should(never()).deleteById(any());
		}

	}

	private Client anExistingClient() {
		Client client = new Client(
				"Maria Silva",
				"12345678909",
				new BigDecimal("6500.00"),
				LocalDate.of(1994, 7, 20),
				2);
		ReflectionTestUtils.setField(client, "clientId", EXISTING_CLIENT_ID);
		return client;
	}

	private ClientDTO aClientDTO(String name) {
		return new ClientDTO(null, name, "12345678909", new BigDecimal("6500.00"), LocalDate.of(1994, 7, 20), 2);
	}

}
