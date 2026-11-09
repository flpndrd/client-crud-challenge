package com.devsuperior.clientcrud.client.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClientControllerIT {

	private static final Long EXISTING_CLIENT_ID = 1L;

	private static final String EXISTING_CLIENT_CPF = "12345678909";

	private static final Long NONEXISTENT_CLIENT_ID = 999L;

	private static final String UNREGISTERED_CPF = "52998224725";

	private static final String CPF_OF_ANOTHER_CLIENT = "98765432100";

	private final MockMvcTester mockMvcTester;

	@Autowired
	ClientControllerIT(MockMvcTester mockMvcTester) {
		this.mockMvcTester = mockMvcTester;
	}

	@Nested
	class Find {

		@Test
		void shouldReturnClientWhenClientExists() {
			MvcTestResult mvcTestResult = mockMvcTester.get().uri("/clients/{clientId}", EXISTING_CLIENT_ID).exchange();

			assertThat(mvcTestResult).hasStatusOk();
			assertThat(mvcTestResult)
					.bodyJson()
					.extractingPath("$.id")
					.convertTo(Long.class)
					.isEqualTo(EXISTING_CLIENT_ID);
			assertThat(mvcTestResult).bodyJson().extractingPath("$.name").isEqualTo("Maria Silva");
		}

		@Test
		void shouldReturnNotFoundWhenClientDoesNotExist() {
			MvcTestResult mvcTestResult = mockMvcTester
					.get()
					.uri("/clients/{clientId}", NONEXISTENT_CLIENT_ID)
					.exchange();

			assertThat(mvcTestResult).hasStatus(HttpStatus.NOT_FOUND);
			assertThat(mvcTestResult).bodyJson().extractingPath("$.detail").isEqualTo("Client not found.");
		}

	}

	@Nested
	class ListClients {

		@Test
		void shouldReturnClientPageSortedByName() {
			MvcTestResult mvcTestResult = mockMvcTester.get().uri("/clients?page=0&size=6&sort=name").exchange();

			assertThat(mvcTestResult).hasStatusOk();
			assertThat(mvcTestResult).bodyJson().extractingPath("$.content.length()").isEqualTo(6);
			assertThat(mvcTestResult).bodyJson().extractingPath("$.content[0].name").isEqualTo("Ana Costa");
			assertThat(mvcTestResult).bodyJson().extractingPath("$.page.totalElements").isEqualTo(15);
		}

	}

	@Nested
	class Create {

		@Test
		void shouldReturnCreatedWhenDataIsValid() {
			String requestBody = aClientJson("Maria Silva", UNREGISTERED_CPF, "1994-07-20");

			MvcTestResult mvcTestResult = mockMvcTester
					.post()
					.uri("/clients")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.exchange();

			assertThat(mvcTestResult).hasStatus(HttpStatus.CREATED);
			assertThat(mvcTestResult).headers().containsHeader("Location");
			assertThat(mvcTestResult).bodyJson().extractingPath("$.id").isNotNull();
			assertThat(mvcTestResult).bodyJson().extractingPath("$.cpf").isEqualTo(UNREGISTERED_CPF);
		}

		@Test
		void shouldReturnUnprocessableContentWithFieldMessagesWhenDataIsInvalid() {
			String requestBody = aClientJson("", UNREGISTERED_CPF, "2999-01-01");

			MvcTestResult mvcTestResult = mockMvcTester
					.post()
					.uri("/clients")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.exchange();

			assertThat(mvcTestResult).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
			assertThat(mvcTestResult)
					.bodyJson()
					.extractingPath("$.errors[?(@.fieldName == 'name')].message")
					.asArray()
					.containsExactly("Name is required");
			assertThat(mvcTestResult)
					.bodyJson()
					.extractingPath("$.errors[?(@.fieldName == 'birthDate')].message")
					.asArray()
					.containsExactly("Birth date cannot be in the future");
		}

		@Test
		void shouldReturnConflictWhenCpfIsRegistered() {
			String requestBody = aClientJson("Maria Silva", CPF_OF_ANOTHER_CLIENT, "1994-07-20");

			MvcTestResult mvcTestResult = mockMvcTester
					.post()
					.uri("/clients")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.exchange();

			assertThat(mvcTestResult).hasStatus(HttpStatus.CONFLICT);
			assertThat(mvcTestResult).bodyJson().extractingPath("$.detail").isEqualTo("CPF is already registered.");
		}

	}

	@Nested
	class Update {

		@Test
		void shouldReturnUpdatedClientWhenDataIsValid() {
			String requestBody = aClientJson("Maria Silvaaa", EXISTING_CLIENT_CPF, "1994-07-20");

			MvcTestResult mvcTestResult = mockMvcTester
					.put()
					.uri("/clients/{clientId}", EXISTING_CLIENT_ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.exchange();

			assertThat(mvcTestResult).hasStatusOk();
			assertThat(mvcTestResult)
					.bodyJson()
					.extractingPath("$.id")
					.convertTo(Long.class)
					.isEqualTo(EXISTING_CLIENT_ID);
			assertThat(mvcTestResult).bodyJson().extractingPath("$.name").isEqualTo("Maria Silvaaa");
		}

		@Test
		void shouldReturnNotFoundWhenClientDoesNotExist() {
			String requestBody = aClientJson("Maria Silvaaa", UNREGISTERED_CPF, "1994-07-20");

			MvcTestResult mvcTestResult = mockMvcTester
					.put()
					.uri("/clients/{clientId}", NONEXISTENT_CLIENT_ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.exchange();

			assertThat(mvcTestResult).hasStatus(HttpStatus.NOT_FOUND);
		}

		@Test
		void shouldReturnUnprocessableContentWithFieldMessagesWhenDataIsInvalid() {
			String requestBody = aClientJson("", EXISTING_CLIENT_CPF, "2999-01-01");

			MvcTestResult mvcTestResult = mockMvcTester
					.put()
					.uri("/clients/{clientId}", EXISTING_CLIENT_ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.exchange();

			assertThat(mvcTestResult).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
			assertThat(mvcTestResult)
					.bodyJson()
					.extractingPath("$.errors[?(@.fieldName == 'name')].message")
					.asArray()
					.containsExactly("Name is required");
			assertThat(mvcTestResult)
					.bodyJson()
					.extractingPath("$.errors[?(@.fieldName == 'birthDate')].message")
					.asArray()
					.containsExactly("Birth date cannot be in the future");
		}

		@Test
		void shouldReturnConflictWhenCpfIsRegisteredForAnotherClient() {
			String requestBody = aClientJson("Maria Silva", CPF_OF_ANOTHER_CLIENT, "1994-07-20");

			MvcTestResult mvcTestResult = mockMvcTester
					.put()
					.uri("/clients/{clientId}", EXISTING_CLIENT_ID)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.exchange();

			assertThat(mvcTestResult).hasStatus(HttpStatus.CONFLICT);
		}

	}

	@Nested
	class Delete {

		@Test
		void shouldReturnNoContentWhenClientExists() {
			MvcTestResult mvcTestResult = mockMvcTester
					.delete()
					.uri("/clients/{clientId}", EXISTING_CLIENT_ID)
					.exchange();

			assertThat(mvcTestResult).hasStatus(HttpStatus.NO_CONTENT);
		}

		@Test
		void shouldReturnNotFoundWhenClientDoesNotExist() {
			MvcTestResult mvcTestResult = mockMvcTester
					.delete()
					.uri("/clients/{clientId}", NONEXISTENT_CLIENT_ID)
					.exchange();

			assertThat(mvcTestResult).hasStatus(HttpStatus.NOT_FOUND);
		}

	}

	private String aClientJson(String name, String cpf, String birthDate) {
		return """
				{
				  "name": "%s",
				  "cpf": "%s",
				  "income": 6500.0,
				  "birthDate": "%s",
				  "children": 2
				}
				""".formatted(name, cpf, birthDate);
	}

}
