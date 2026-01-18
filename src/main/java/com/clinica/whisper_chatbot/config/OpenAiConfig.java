package com.clinica.whisper_chatbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "openai.api") // Ajustado para bater com seu YML (openai.api.key)
@Data
public class OpenAiConfig {

    private String key; // O Spring mapeia 'openai.api.key' para este campo

    @PostConstruct
    public void check() {
        // Se o valor for nulo ou contiver o símbolo $, significa que o Spring não resolveu a variável
        if (key == null || key.isBlank() || key.contains("${")) {
            // Tenta buscar direto do System Property (que você setou no main)
            key = System.getProperty("OPENAI_API_KEY");
        }

        if (key == null || key.isEmpty()) {
            System.err.println("❌ [CONFIG] Chave da OpenAI ainda não disponível no contexto.");
        } else {
            System.out.println("🚀 [CONFIG] OpenAiConfig validada! Pronto para processar voz.");
        }
    }
}