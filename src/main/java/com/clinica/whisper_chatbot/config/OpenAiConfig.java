package com.clinica.whisper_chatbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "openai")
@Data
public class OpenAiConfig {
    // Se o prefixo no application.yml for 'openai' e a chave 'api-key',
    // o Spring procura por 'apiKey' automaticamente.
    private String apiKey;

    @PostConstruct
    public void check() {
        if (apiKey == null || apiKey.equals("${OPENAI_API_KEY}")) {
            System.err.println("CRÍTICO: A chave da OpenAI não foi carregada corretamente!");
        } else {
            // Mostra apenas os 5 primeiros caracteres por segurança
            System.out.println("OPENAI_API_KEY carregada com sucesso: " + apiKey.substring(0, 5) + "...");
        }
    }
}