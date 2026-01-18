package com.clinica.whisper_chatbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.List;

@Data
@Component
@Slf4j
public class AtendenteConfig {
    private String nome;
    private String personalidade;
    private String idioma;
    private String respostaInicial;
    private String instrucoesSistema; // Novo campo mapeado do JSON
    private List<String> diretrizes;   // Alterado para List para facilitar o String.join

    @PostConstruct
    public void carregarConfiguracao() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new ClassPathResource("config/atendente.json").getInputStream();
            AtendenteConfig aux = mapper.readValue(is, AtendenteConfig.class);

            this.nome = aux.nome;
            this.personalidade = aux.personalidade;
            this.idioma = aux.idioma;
            this.respostaInicial = aux.respostaInicial;
            this.instrucoesSistema = aux.instrucoesSistema;
            this.diretrizes = aux.diretrizes;

            log.info("[SUCESSO] Configurações do Eduardo carregadas via JSON.");
        } catch (Exception e) {
            log.error("[ERRO] Falha ao carregar atendente.json: {}", e.getMessage());
        }
    }
}