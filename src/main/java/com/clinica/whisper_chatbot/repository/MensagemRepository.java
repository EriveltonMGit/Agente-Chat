package com.clinica.whisper_chatbot.repository;

import com.clinica.whisper_chatbot.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
}