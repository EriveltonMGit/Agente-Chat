package com.clinica.whisper_chatbot;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WhisperChatbotApplication {

    public static void main(String[] args) {
        // Tenta carregar o .env do diretório de trabalho atual
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./") // Força a busca na raiz do projeto
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
                // Log de depuração (opcional, remova depois)
                if(entry.getKey().contains("OPENAI")) {
                    System.out.println("[DEBUG] Carregando chave do .env...");
                }
            });
        } catch (Exception e) {
            System.err.println("[ERRO] Falha ao carregar .env: " + e.getMessage());
        }

        SpringApplication.run(WhisperChatbotApplication.class, args);

        System.out.println("\n>>> EDUARDO ONLINE E PRONTO NO PORTA 8080 <<<");
    }
}