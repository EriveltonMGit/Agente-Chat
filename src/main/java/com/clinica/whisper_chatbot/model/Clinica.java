package com.clinica.whisper_chatbot.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "clinicas")
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Clinica implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "cnes")
    private String cnes;

    @Column(name = "telefone")
    private String telefone;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @ManyToMany(mappedBy = "clinicasAtende", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @JsonManagedReference
    private Set<Medico> medicos;


}
