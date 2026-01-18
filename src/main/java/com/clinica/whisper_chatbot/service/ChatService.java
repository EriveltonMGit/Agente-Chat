package com.clinica.whisper_chatbot.service;

import com.clinica.whisper_chatbot.config.AtendenteConfig;
import com.clinica.whisper_chatbot.config.OpenAiConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ChatService {

    private final AgendaService agendaService;
    private final OpenAiConfig config;
    private final AtendenteConfig atendenteConfig;
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();
    private final Map<String, List<JSONObject>> contexto = new HashMap<>();

    public ChatService(OpenAiConfig config, AtendenteConfig atendenteConfig, AgendaService agendaService) {
        this.config = config;
        this.atendenteConfig = atendenteConfig;
        this.agendaService = agendaService;
    }

    /**
     * Conversa com a LLM e retorna resposta natural
     */
    public String conversar(String sessionId, String texto) {
        contexto.putIfAbsent(sessionId, new ArrayList<>());
        List<JSONObject> historico = contexto.get(sessionId);

        try {
            // --- Inicialização do contexto ---
            if (historico.isEmpty()) {
                String infoAgenda = agendaService.obterResumoAgenda(113);
                String systemPrompt = atendenteConfig.getInstrucoesSistema()
                        .replace("{nome}", atendenteConfig.getNome())
                        .replace("{agenda}", infoAgenda)
                        .replace("{personalidade}", atendenteConfig.getPersonalidade())
                        .replace("{data_hoje}", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .replace("{diretrizes}", String.join(" ", atendenteConfig.getDiretrizes()));
                historico.add(new JSONObject().put("role", "system").put("content", systemPrompt));
            }

            historico.add(new JSONObject().put("role", "user").put("content", texto));

            // Limita histórico para performance
            if (historico.size() > 10) historico.subList(1, historico.size() - 8).clear();

            JSONArray msgs = new JSONArray();
            historico.forEach(msgs::put);

            JSONObject payload = new JSONObject()
                    .put("model", "gpt-4o-mini")
                    .put("temperature", 0.5)  // ligeiramente mais criativo
                    .put("max_tokens", 150)
                    .put("messages", msgs);

            RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + config.getApiKey())
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) return "Erro de conexão.";
                JSONObject json = new JSONObject(response.body().string());
                String resposta = json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content").trim();

                // --- Detecta e processa agendamento ---
                if (resposta.contains("[AGENDAR:")) {
                    try {
                        String dataStr = resposta.substring(resposta.indexOf("[AGENDAR:") + 9, resposta.indexOf("]")).trim();
                        LocalDateTime horario = LocalDateTime.parse(dataStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        agendaService.realizarAgendamento(113, 200, horario);
                        log.info("[DATABASE] Agendamento confirmado para {}", dataStr);

                        // Remove tag técnica para a fala
                        resposta = resposta.replaceAll("\\[AGENDAR:.*?\\]", "").trim();
                    } catch (Exception e) {
                        log.error("[ERRO-PARSER] Falha ao ler data da IA: {}", e.getMessage());
                    }
                }

                // --- Ajuste de naturalidade ---
                resposta = resposta.replaceAll("\\. ", ".\n"); // quebra frases curtas
                resposta = "😊 " + resposta; // adiciona emoji para humanizar

                historico.add(new JSONObject().put("role", "assistant").put("content", resposta));
                return resposta;
            }

        } catch (Exception e) {
            log.error("[CHAT-ERRO] {}", e.getMessage());
            return "Ops! Tivemos um probleminha técnico, mas já estou voltando 😉";
        }
    }

    public void reset(String sessionId) { contexto.remove(sessionId); }
    public String conversar(String texto) { return conversar("default", texto); }
}
