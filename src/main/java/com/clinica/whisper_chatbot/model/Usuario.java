package com.clinica.whisper_chatbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@DynamicUpdate
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;


    @Column(name = "codigo_verificacao", length = 10)
    private String codigoVerificacao;

    @Column(name = "token_recuperacao_senha")
    private String tokenRecuperacaoSenha;

    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "senha", length = 255, nullable = true)
    private String senha;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "administrador_conta")
    private Boolean administradorConta;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;

    @Column(name = "cpf", unique = true, nullable = true, length = 14)
    private String cpf;



    @Column(name = "plano_data_inicio")
    private LocalDate planoDataInicio;

    @Column(name = "plano_data_fim")
    private LocalDate planoDataFim;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuarios_clinicas",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "clinica_id")
    )
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private Set<Clinica> clinicasAtende;
}
