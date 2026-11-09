package com.devsuperior.clientcrud.client.exception;

import java.io.Serial;

import com.devsuperior.clientcrud.exception.ResourceNotFoundException;

public class ClientNotFoundException extends ResourceNotFoundException {

	@Serial
	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = "Client not found.";

	public ClientNotFoundException() {
		super(MESSAGE);
	}

}
