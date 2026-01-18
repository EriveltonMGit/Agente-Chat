package com.clinica.whisper_chatbot.model;


import com.clinica.whisper_chatbot.util.ConvenioEnum;
import com.clinica.whisper_chatbot.util.SexoEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "pacientes")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Paciente extends Usuario {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_id")
    @JsonBackReference
    private Clinica clinica;

    @Column(name = "nome_mae", length = 100)
    private String nomeMae;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_responsavel_id")
    private Medico medicoResponsavel;

    @Column(name = "licenca_link", length = 255)
    private String licencaLink;

    @Column(name = "estado_civil", length = 20)
    private String estadoCivil;

    @Column(name = "telefone_fixo", length = 20)
    private String telefoneFixo;

    @Enumerated(EnumType.STRING)
    @Column(name = "convenio")
    private ConvenioEnum convenio;

    @Column(name = "numero_carteirinha", length = 50)
    private String numeroCarteirinha;

    @Column(name = "cns", length = 15)
    private String cns; // Cartão Nacional de Saúde

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo")
    private SexoEnum sexo;

    @Column(name = "profissao", length = 100)
    private String profissao;

    @Column(name = "naturalidade", length = 100)
    private String naturalidade;

    @Column(name = "queixa_principal", columnDefinition = "TEXT")
    private String queixaPrincipal;

    @Column(name = "diagnostico_base", columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "cid_base", length = 10)
    private String cid;

    @Column(name = "rg", length = 20)
    private String rg;

    @Column(name = "is_obito", nullable = false)
    private Boolean isObito;

    @Column(name = "alergias", columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "doencas", columnDefinition = "TEXT")
    private String doencas;

    @Column(name = "medicamentos_uso_continuo", columnDefinition = "TEXT")
    private String medicamentosUsoContinuo;

    @Column(name = "tipo_sanguineo", length = 5)
    private String tipoSanguineo;

    @Column(name = "doador_orgaos")
    private Boolean doadorOrgaos;

    @Column(name = "fumante")
    private Boolean fumante;

    @Column(name = "cigarros_dia")
    private Integer cigarrosDia;

    @Column(name = "tempo_fumante", length = 50)
    private String tempoFumante;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    // Construtor atualizado
    public Paciente(String nome, String telefone, LocalDate dataNascimento, ConvenioEnum convenio) {
        this.setNome(nome);
        this.setTelefone(telefone);
        this.setDataNascimento(dataNascimento);
        this.setConvenio(convenio);
        this.isObito = false;
        this.setAtivo(true);
    }
}
