package com.clinica.whisper_chatbot.util;

import lombok.Getter;

@Getter
public enum StatusAgendamentoEnum {
    AGENDADO("Agendado"),
    CONFIRMADO("Confirmado"),
    CANCELADO("Cancelado"),
    FINALIZADO("Finalizado"),
    NAO_COMPARECEU("Não Compareceu"),
    BLOQUEADO("Bloqueado"); // NOVO STATUS

    private final String descricao;

    StatusAgendamentoEnum(String descricao) {
        this.descricao = descricao;
    }
}
