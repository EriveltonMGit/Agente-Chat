package com.clinica.whisper_chatbot.util;

import lombok.Getter;

@Getter
public enum SexoEnum {
    // 1. Defina as constantes da enumeração com seus respectivos nomes completos.
    MASCULINO("Masculino"),
    FEMININO("Feminino");

    // 2. Declare o campo para o nome completo.
    private final String nomeCompleto;

    // 3. Crie o construtor para o enum.
    SexoEnum(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }
}