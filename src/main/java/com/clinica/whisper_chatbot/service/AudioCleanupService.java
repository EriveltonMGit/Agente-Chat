package com.clinica.whisper_chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

@Slf4j
@Service
public class AudioCleanupService {

    private static final String BASE = "C:\\Users\\erive\\Desktop\\Release\\audios";

    /**
     * Apaga todos os arquivos de áudio da pasta de forma segura.
     */
    public void limparPastaAudios() {
        Path pasta = Paths.get(BASE);

        if (!Files.exists(pasta)) {
            log.warn("[CLEANUP] Pasta de áudios não existe: {}", BASE);
            return;
        }

        try (DirectoryStream<Path> arquivos = Files.newDirectoryStream(pasta, "*.wav")) {
            for (Path arquivo : arquivos) {
                try {
                    Files.delete(arquivo);
                    log.info("[CLEANUP] Arquivo deletado: {}", arquivo.getFileName());
                } catch (IOException e) {
                    log.error("[CLEANUP-ERRO] Não foi possível deletar {}: {}", arquivo.getFileName(), e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("[CLEANUP-ERRO] Falha ao listar arquivos da pasta: {}", e.getMessage());
        }
    }
}
