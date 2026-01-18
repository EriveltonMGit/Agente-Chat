package com.clinica.whisper_chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WhisperService {

    private static final String BASE = "C:\\Users\\erive\\Desktop\\Release\\";
    private static final String FFMPEG_EXE  = BASE + "ffmpeg.exe";
    private static final String WHISPER_EXE = BASE + "whisper-cli.exe";
    private static final String PIPER_EXE   = BASE + "piper.exe";

    // Otimização: Se você tiver o ggml-base.bin, a velocidade será 3x maior que o small
    private static final String MODEL_PATH  = BASE + "models\\ggml-small.bin";

    public String prepararAudio(String inputWav) {
        long inicio = System.currentTimeMillis();
        String output = inputWav.replace(".wav", "_ready.wav");
        try {
            log.info("[FFMPEG] Iniciando normalização...");
            ProcessBuilder pb = new ProcessBuilder(
                    FFMPEG_EXE, "-i", inputWav, "-ar", "16000", "-ac", "1", "-c:a", "pcm_s16le", "-y", output
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.waitFor(6, TimeUnit.SECONDS);

            long fim = System.currentTimeMillis();
            log.info("[TIMER] FFmpeg demorou: {}ms", (fim - inicio));
            return output;
        } catch (Exception e) {
            log.error("[FFMPEG-ERRO] {}", e.getMessage());
            return inputWav;
        }
    }

    public String processarAudio(String audioPath) {
        long inicio = System.currentTimeMillis();
        log.info("[WHISPER] Iniciando transcrição com 12 threads...");
        StringBuilder buffer = new StringBuilder();
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    WHISPER_EXE,
                    "-m", MODEL_PATH,
                    "-f", audioPath,
                    "--language", "pt",
                    "--no-timestamps",
                    "--threads", "12", // Uso total do i5-14500
                    "--beam-size", "1"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = r.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
            }
            p.waitFor(12, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("[WHISPER-FAIL] {}", e.getMessage());
            return "";
        }

        String texto = buffer.toString().lines()
                .filter(l -> l.trim().length() > 0 && !l.contains("loading") && !l.contains("whisper_init") && !l.contains("timings"))
                .reduce((a, b) -> b).orElse("").trim();

        texto = texto.replaceAll("\\s+", " ").replaceAll("[^\\p{L}\\p{N}\\s\\?\\!\\,\\.]", "").trim();

        long fim = System.currentTimeMillis();
        log.info("[TIMER] Whisper demorou: {}ms | Texto: \"{}\"", (fim - inicio), texto);
        return texto;
    }

    public void gerarRespostaVoz(String texto, String caminhoSaida) {
        long inicio = System.currentTimeMillis();
        try {
            log.info("[PIPER] Iniciando síntese (Jeff)...");
            String modelVoz = PiperService.VOICE;

            ProcessBuilder pb = new ProcessBuilder(
                    PIPER_EXE,
                    "--model", modelVoz,
                    "--output_file", caminhoSaida,
                    "--length_scale", "0.9" // Acelera levemente a fala
            );

            pb.redirectInput(ProcessBuilder.Redirect.PIPE);
            Process p = pb.start();

            try (OutputStreamWriter writer = new OutputStreamWriter(p.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(texto);
                writer.flush();
            }

            p.waitFor(10, TimeUnit.SECONDS);
            long fim = System.currentTimeMillis();
            log.info("[TIMER] Piper demorou: {}ms", (fim - inicio));
        } catch (Exception e) {
            log.error("[PIPER-ERRO] Falha na síntese: {}", e.getMessage());
        }
    }
}

//correto