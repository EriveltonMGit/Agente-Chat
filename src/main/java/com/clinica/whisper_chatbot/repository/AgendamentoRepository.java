package com.clinica.whisper_chatbot.repository;

import com.clinica.whisper_chatbot.model.Agendamento;
import com.clinica.whisper_chatbot.util.StatusAgendamentoEnum; // Importe o Enum aqui
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {

    // Ajustado para usar o Enum na consulta
    @Query("SELECT a FROM Agendamento a WHERE a.medico.id = :medicoId AND a.status = com.clinica.whisper_chatbot.util.StatusAgendamentoEnum.AGENDADO")
    List<Agendamento> findAgendaAtiva(@Param("medicoId") Integer medicoId);

    // CORREÇÃO AQUI: Troque String por StatusAgendamentoEnum
    boolean existsByMedicoIdAndDataHoraAndStatus(Integer medicoId, LocalDateTime dataHora, StatusAgendamentoEnum status);
}