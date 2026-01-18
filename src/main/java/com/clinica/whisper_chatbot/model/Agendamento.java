package com.clinica.whisper_chatbot.model;

import com.clinica.whisper_chatbot.util.ConvenioEnum;
import com.clinica.whisper_chatbot.util.StatusAgendamentoEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relacionamentos mantidos para integridade do banco
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", referencedColumnName = "id_usuario")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", referencedColumnName = "id_usuario", nullable = false)
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_id", nullable = false)
    private Clinica clinica;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "data_termino", nullable = false)
    private LocalDateTime dataTermino;

    @Column(name = "duracao_em_minutos", nullable = false)
    private Integer duracaoEmMinutos;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAgendamentoEnum status;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @Column(name = "procedimentos", columnDefinition = "TEXT")
    private String procedimentos;

    @Enumerated(EnumType.STRING)
    @Column(name = "convenio_agendamento")
    private ConvenioEnum convenio;

    @Column(name = "recorrencia", length = 50)
    private String recorrencia;

    @Column(name = "gerar_link_pagamento", nullable = false)
    private Boolean gerarLinkPagamento = false;

    @Column(name = "is_bloqueio", nullable = false)
    private Boolean isBloqueio = false;

    @Column(name = "motivo_bloqueio", length = 255)
    private String motivoBloqueio;

    @Column(name = "lembrete_enviado", nullable = false)
    private Boolean lembreteEnviado = false;


    @PrePersist
    public void prePersist() {
        if (this.gerarLinkPagamento == null) this.gerarLinkPagamento = false;
        if (this.isBloqueio == null) this.isBloqueio = false;
        if (this.lembreteEnviado == null) this.lembreteEnviado = false;
    }
}