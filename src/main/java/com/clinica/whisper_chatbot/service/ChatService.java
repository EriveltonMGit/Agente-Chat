package com.clinica.whisper_chatbot.service;

import com.clinica.whisper_chatbot.config.OpenAiConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
@Slf4j
@Service
public class ChatService {

    private final OpenAiConfig config;
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";

    // Reutilizar o cliente HTTP evita a criação de novos sockets em cada chamada, economizando tempo de conexão
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS) // Tempo de conexão reduzido para falha rápida
            .writeTimeout(3, TimeUnit.SECONDS)
            .readTimeout(7, TimeUnit.SECONDS)   // Reduzido para acelerar a percepção de resposta
            .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
            .build();

    private final Map<String, List<JSONObject>> contexto = new HashMap<>();

    public ChatService(OpenAiConfig config) {
        this.config = config;
    }

    public String conversar(String sessionId, String texto) {
        long inicio = System.currentTimeMillis(); // marca início
        try {
            contexto.putIfAbsent(sessionId, new ArrayList<>());
            List<JSONObject> historico = contexto.get(sessionId);

            if (historico.isEmpty()) {
                historico.add(new JSONObject()
                        .put("role", "system")
                        .put("content", """
                        Você é uma assistente clínica brasileira direta e gentil.
                        
                        Diretrizes de VELOCIDADE:
                        - Respostas extremamente curtas (máximo 2 frases).
                        - Não use introduções formais longas.
                        - Vá direto ao ponto para que a síntese de voz seja rápida.
                        - Se o paciente disser pouco, apenas confirme e pergunte o próximo passo.
                    """));
            }

            historico.add(new JSONObject().put("role", "user").put("content", texto));

            // Mantém apenas o essencial para reduzir o processamento da API
            if (historico.size() > 6) {
                historico.subList(1, historico.size() - 4).clear();
            }

            JSONArray msgs = new JSONArray();
            historico.forEach(msgs::put);

            JSONObject payload = new JSONObject()
                    .put("model", "gpt-4o-mini")
                    .put("temperature", 0.3)
                    .put("max_tokens", 100)
                    .put("presence_penalty", 0.6)
                    .put("messages", msgs);

            RequestBody body = RequestBody.create(
                    payload.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + config.getApiKey())
                    .build();

            String resposta;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("[CHAT] Resposta não foi bem-sucedida: HTTP {}", response.code());
                    return "Erro na rede, pode repetir?";
                }

                JSONObject json = new JSONObject(response.body().string());
                resposta = json.getJSONArray("choices").getJSONObject(0)
                        .getJSONObject("message").getString("content").trim();

                historico.add(new JSONObject().put("role", "assistant").put("content", resposta));
            }

            long fim = System.currentTimeMillis();
            log.info("[TIMER] LLM demorou: {}ms | Input: \"{}\" | Resposta: \"{}\"", (fim - inicio), texto, resposta);

            return resposta;

        } catch (Exception e) {
            long fim = System.currentTimeMillis();
            log.error("[CHAT-ERRO] Exceção após {}ms: {}", (fim - inicio), e.getMessage());
            return "Ops! Tivemos um soluço técnico. Repete pra mim?";
        }
    }


    public void reset(String sessionId) { contexto.remove(sessionId); }
    public String conversar(String texto) { return conversar("default", texto); }
}