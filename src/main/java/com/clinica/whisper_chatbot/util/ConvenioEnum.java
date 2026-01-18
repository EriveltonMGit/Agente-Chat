package com.clinica.whisper_chatbot.util;

import lombok.Getter;

@Getter
public enum ConvenioEnum {
    PARTICULAR("Particular"),
    PLANO_DE_SAUDE("Plano de Saúde"),
    GOVERNO("Governo");

    private final String descricao;

    ConvenioEnum(String descricao) {
        this.descricao = descricao;
    }
}
