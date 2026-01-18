package com.clinica.whisper_chatbot.service;

import com.clinica.whisper_chatbot.model.Agendamento;
import com.clinica.whisper_chatbot.model.Clinica;
import com.clinica.whisper_chatbot.model.Medico;
import com.clinica.whisper_chatbot.model.Paciente;
import com.clinica.whisper_chatbot.repository.AgendamentoRepository;
import com.clinica.whisper_chatbot.util.StatusAgendamentoEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendaService {

    @Autowired
    private AgendamentoRepository repository;

    public String obterResumoAgenda(Integer medicoId) {
        List<Agendamento> agendamentos = repository.findAgendaAtiva(medicoId);
        if (agendamentos.isEmpty()) {
            return "A agenda deste médico está totalmente livre.";
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm");
        String horariosOcupados = agendamentos.stream()
                .map(a -> a.getDataHora().format(fmt))
                .collect(Collectors.joining(", "));

        return "Os seguintes horários já estão ocupados: " + horariosOcupados;
    }

    @Transactional
    public String realizarAgendamento(Integer medicoId, Integer pacienteId, LocalDateTime dataHora) {
        // CORREÇÃO: Passando o Enum real em vez de String para evitar o erro de 'incompatible type'
        boolean ocupado = repository.existsByMedicoIdAndDataHoraAndStatus(
                medicoId,
                dataHora,
                StatusAgendamentoEnum.AGENDADO
        );

        if (ocupado) {
            return "Sinto muito, mas este horário acaba de ser preenchido por outro paciente.";
        }

        // 2. Cria o novo objeto de agendamento com as associações corretas
        Agendamento novo = new Agendamento();

        // Criando proxies para as chaves estrangeiras (IDs 113 e 200 do seu script SQL)
        Medico medico = new Medico(); medico.setId(medicoId);
        Paciente paciente = new Paciente(); paciente.setId(pacienteId);
        Clinica clinica = new Clinica(); clinica.setId(1);

        novo.setMedico(medico);
        novo.setPaciente(paciente);
        novo.setClinica(clinica);

        novo.setDataHora(dataHora);
        novo.setDataTermino(dataHora.plusMinutes(30));
        novo.setDuracaoEmMinutos(30);
        novo.setStatus(StatusAgendamentoEnum.AGENDADO);

        // Garantindo que campos booleanos não fiquem nulos
        novo.setGerarLinkPagamento(false);
        novo.setIsBloqueio(false);
        novo.setLembreteEnviado(false);

        repository.save(novo);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm");
        return "Confirmado! Sua consulta foi agendada para o dia " + dataHora.format(fmt);
    }
}