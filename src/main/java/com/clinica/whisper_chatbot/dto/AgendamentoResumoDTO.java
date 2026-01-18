package com.clinica.whisper_chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class AgendamentoResumoDTO {
    private String medicoNome;
    private String dataHora;
    private String status;

    public String toStringParaIA() {
        return String.format("Médico: %s | Horário: %s | Status: %s",
                medicoNome, dataHora, status);
    }
}