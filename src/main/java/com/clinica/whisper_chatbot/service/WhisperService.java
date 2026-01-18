package com.clinica.whisper_chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WhisperService {

    private static final String BASE = "C:\\Users\\erive\\Desktop\\Release\\";
    private static final String FFMPEG_EXE  = BASE + "ffmpeg.exe";
    private static final String WHISPER_EXE = BASE + "whisper-cli.exe";
    public static final String MODEL_PATH  = BASE + "models\\ggml-base.bin";

    /**
     * Normaliza áudio (16kHz mono)
     */
    public String prepararAudio(String inputWav) {
        String output = inputWav.replace(".wav", "_ready.wav");
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    FFMPEG_EXE, "-i", inputWav, "-ar", "16000", "-ac", "1", "-c:a", "pcm_s16le", "-y", output
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            if (!p.waitFor(5, TimeUnit.SECONDS)) p.destroyForcibly();
            return output;
        } catch (Exception e) {
            log.error("[FFMPEG-ERRO] {}", e.getMessage());
            return inputWav;
        }
    }

    /**
     * Transcreve áudio para texto
     */
    public String processarAudio(String audioPath) {
        StringBuilder buffer = new StringBuilder();
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    WHISPER_EXE,
                    "-m", MODEL_PATH,
                    "-f", audioPath,
                    "--language", "pt",
                    "--no-timestamps",
                    "--threads", "12",
                    "--beam-size", "1"
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();

            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = r.readLine()) != null) {
                    if (line.isBlank() || line.contains("whisper") || line.contains("model") || line.contains("system_info")) continue;
                    buffer.append(line).append(" ");
                }
            }
            p.waitFor(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("[WHISPER-FAIL] {}", e.getMessage());
            return "";
        }

        // Limpeza final para envio à LLM
        return buffer.toString().replaceAll("\\s+", " ").replaceAll("[^\\p{L}\\p{N}\\s\\?\\!\\,\\.]", "").trim();
    }
}
