package com.clinica.whisper_chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WhisperChatbotApplication {

    public static void main(String[] args) {
        // Inicia o servidor Tomcat na porta 8080
        SpringApplication.run(WhisperChatbotApplication.class, args);

        System.out.println("##############################################");
        System.out.println("# Chatbot da Clínica Online e aguardando áudio #");
        System.out.println("# Acesse: http://localhost:8080/index.html   #");
        System.out.println("##############################################");
    }

    // O @Bean CommandLineRunner foi removido daqui para evitar
    // buscar o arquivo fixo "audio_real.mp3" ao iniciar
}