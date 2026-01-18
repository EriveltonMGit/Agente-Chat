package com.clinica.whisper_chatbot;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WhisperChatbotApplication {

    public static void main(String[] args) {
        // Log para saber onde o Java está "pisando"
        String diretorioAtual = System.getProperty("user.dir");
        System.out.println("📂 O Eduardo está procurando o .env em: " + diretorioAtual);

        // 1. Carrega o .env
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();

        // 2. Injeta no System
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        // 3. Verificação IMEDIATA (Antes do Spring subir)
        String key = System.getProperty("OPENAI_API_KEY");
        if (key == null || key.isEmpty()) {
            System.err.println("❌ ERRO: OPENAI_API_KEY não encontrada no .env!");
            System.err.println("👉 Certifique-se que o arquivo .env está em: " + diretorioAtual);
        } else {
            System.out.println("✅ Chave OpenAI detectada (Início: " + key.substring(0, 7) + "...)");
        }

        SpringApplication.run(WhisperChatbotApplication.class, args);
    }
}