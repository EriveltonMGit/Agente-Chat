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
     * Sintetiza um texto em WAV usando o Piper.
     *
     * @param texto      Texto a ser convertido em voz.
     * @param outputWav  Caminho do arquivo WAV de saída.
     * @throws Exception Se ocorrer algum erro na execução do Piper.
     */
    public void sintetizar(String texto, String outputWav) throws Exception {
        Path pModel = Paths.get(VOICE);
        if (!Files.exists(pModel)) {
            log.error("[PIPER-ERRO] Modelo de voz não encontrado: {}", VOICE);
            return;
        }

        log.info("[PIPER] Sintetizando: {}", texto);

        ProcessBuilder pb = new ProcessBuilder(
                PIPER_EXE,
                "--model", VOICE,
                "--output_file", outputWav,
                "--length_scale", "0.9"
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Envia o texto via STDIN
        try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(texto);
            writer.flush();
        }

        // Lê logs de stdout/erros
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("[PIPER-LOG] {}", line);
            }
        }

        // Aguarda término do processo com timeout
        boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        int code = finished ? process.exitValue() : -1;
        log.info("[PIPER] Finalizado com código {}", code);

        if (code != 0) {
            throw new RuntimeException("Piper retornou código de erro: " + code);
        }
    }
}

//correto