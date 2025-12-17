package com.oficina.backend.exception;

public class CpfDuplicadoException extends RuntimeException{
    public CpfDuplicadoException(String cpf){
        super("Já existe um cliente cadastrado com o CPF: " + cpf);
    }
}
