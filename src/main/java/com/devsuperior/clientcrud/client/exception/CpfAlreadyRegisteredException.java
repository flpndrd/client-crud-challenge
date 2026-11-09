package com.devsuperior.clientcrud.client.exception;

import java.io.Serial;

import com.devsuperior.clientcrud.exception.BusinessRuleException;

public class CpfAlreadyRegisteredException extends BusinessRuleException {

	@Serial
	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = "CPF is already registered.";

	public CpfAlreadyRegisteredException() {
		super(MESSAGE);
	}

}
