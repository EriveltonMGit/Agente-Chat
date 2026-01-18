package com.clinica.whisper_chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PiperService {

    private static final String PIPER_EXE = "C:\\Users\\erive\\Desktop\\Release\\piper.exe";
    private static final String BASE = "C:\\Users\\erive\\Desktop\\Release\\";
    public static final String VOICE = BASE + "voices\\pt_BR-cadu-medium.onnx";

    /**
     * Sintetiza voz a partir do texto natural
     */
    public void sintetizar(String texto, String outputWav) throws Exception {
        Path pModel = Paths.get(VOICE);
        if (!Files.exists(pModel)) {
            log.error("[PIPER-ERRO] Modelo de voz não encontrado: {}", VOICE);
            return;
        }

        log.info("[PIPER] Sintetizando: {}", texto);

        // Configuração para voz mais humana
        ProcessBuilder pb = new ProcessBuilder(
                PIPER_EXE,
                "--model", VOICE,
                "--output_file", outputWav,
                "--length_scale", "1.05",       // ligeiramente mais lento
                "--sentence_silence", "0.3",    // pausa entre frases
                "--noise_scale", "0.5"          // suaviza a voz
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(texto);
            writer.flush();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("[PIPER-LOG] {}", line);
            }
        }

        boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        int code = finished ? process.exitValue() : -1;
        log.info("[PIPER] Finalizado com código {}", code);

        if (code != 0) {
            throw new RuntimeException("Piper retornou código de erro: " + code);
        }
    }
}
