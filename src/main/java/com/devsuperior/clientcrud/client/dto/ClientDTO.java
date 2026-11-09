package com.devsuperior.clientcrud.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import com.devsuperior.clientcrud.client.entity.Client;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record ClientDTO(
		@JsonProperty("id")
		Long clientId,
		@NotBlank(message = "Name is required")
		String name,
		@NotBlank(message = "CPF is required")
		@CPF(message = "CPF is invalid")
		@Pattern(regexp = "\\d{11}", message = "CPF must contain only numbers")
		String cpf,
		@NotNull(message = "Income is required")
		@PositiveOrZero(message = "Income cannot be negative")
		BigDecimal income,
		@NotNull(message = "Birth date is required")
		@PastOrPresent(message = "Birth date cannot be in the future")
		LocalDate birthDate,
		@NotNull(message = "Number of children is required")
		@Min(value = 0, message = "Number of children cannot be negative")
		Integer children) {

	public static ClientDTO from(Client client) {
		return new ClientDTO(
				client.getClientId(),
				client.getName(),
				client.getCpf(),
				client.getIncome(),
				client.getBirthDate(),
				client.getChildren());
	}

}
