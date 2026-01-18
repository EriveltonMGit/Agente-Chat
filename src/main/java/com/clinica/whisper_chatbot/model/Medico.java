package com.clinica.whisper_chatbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Medico extends Usuario {

    @Column(name = "registro", unique = true, nullable = false)
    private String registro;

    @Column(name = "especialidade")
    private String especialidade;

    @Column(name = "conselho")
    private String conselho;

    @Column(name = "uf")
    private String uf;

    @Column(name = "profissao")
    private String profissao;

    @Column(name = "cbo")
    private String cbo;

    @Column(name = "cnes")
    private String cnes;

    @Column(name = "genero")
    private String genero;

    @Column(name = "tratamento")
    private String tratamento;
}