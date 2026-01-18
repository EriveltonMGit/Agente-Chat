package com.clinica.whisper_chatbot.controler;

import com.clinica.whisper_chatbot.model.Teste;
import com.clinica.whisper_chatbot.repository.TesteRepository;
import com.clinica.whisper_chatbot.service.AudioCleanupService;
import com.clinica.whisper_chatbot.service.ChatService;
import com.clinica.whisper_chatbot.service.PiperService;
import com.clinica.whisper_chatbot.service.WhisperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/whisper")
public class WhisperController {

    private final WhisperService whisperService;
    private final PiperService piperService;
    private final ChatService chatService;
    private final TesteRepository repository;
    private final AudioCleanupService audioCleanupService; // injetado

    private static final String BASE_PATH = "C:\\Users\\erive\\Desktop\\Release\\";

    public WhisperController(
            WhisperService whisperService,
            PiperService piperService,
            ChatService chatService,
            TesteRepository repository,
            AudioCleanupService audioCleanupService // injetado
    ) {
        this.whisperService = whisperService;
        this.piperService = piperService;
        this.chatService = chatService;
        this.repository = repository;
        this.audioCleanupService = audioCleanupService;
    }

    @PostMapping("/conversar")
    public ResponseEntity<Resource> conversar(@RequestParam("file") MultipartFile file) {

        String id = UUID.randomUUID().toString();
        Path pastaAudios = Paths.get(BASE_PATH, "audios");

        try {
            if (!Files.exists(pastaAudios)) {
                Files.createDirectories(pastaAudios);
            }
        } catch (Exception e) {
            log.error("[ERRO] Não foi possível criar a pasta de áudios:", e);
            return ResponseEntity.status(500).build();
        }

        String pathIn  = pastaAudios.resolve(id + "_in.wav").toString();
        String pathOut = pastaAudios.resolve(id + "_out.wav").toString();

        try {
            log.info("[START] Recebendo áudio…");
            Files.copy(file.getInputStream(), Paths.get(pathIn), StandardCopyOption.REPLACE_EXISTING);

            // 1. Normaliza com ffmpeg
            String pathPronto = whisperService.prepararAudio(pathIn);

            // 2. Whisper → Texto
            String texto = whisperService.processarAudio(pathPronto);

            if (texto == null || texto.isBlank()) {
                log.warn("[WARN] Whisper não entendeu nada.");
                return ResponseEntity.noContent().build();
            }

            log.info("[USER]: {}", texto);

            // 3. ChatGPT / LLM responde
            String resposta = chatService.conversar(texto);
            log.info("[BOT]: {}", resposta);

            // 4. Salva no banco apenas a transcrição
            repository.save(new Teste(null, "User: " + texto + " | Bot: " + resposta));

            // 5. Piper → TTS
            piperService.sintetizar(resposta, pathOut);

            // 6. Aguarda WAV pronto
            Path pOut = Paths.get(pathOut);
            int retry = 0;
            while (retry < 25) {
                if (Files.exists(pOut) && Files.size(pOut) > 1500) break;
                Thread.sleep(150);
                retry++;
            }

            if (!Files.exists(pOut)) {
                log.error("[FAIL] Piper não gerou WAV: {}", pathOut);
                return ResponseEntity.status(500).build();
            }

            byte[] wav = Files.readAllBytes(pOut);
            log.info("[SUCCESS] Resposta enviada ({} bytes)", wav.length);

            // 7. Limpa todos os áudios da pasta
            audioCleanupService.limparPastaAudios();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/wav"))
                    .body(new ByteArrayResource(wav));

        } catch (Exception e) {
            log.error("[CRITICAL] Erro no fluxo:", e);
            return ResponseEntity.status(500).build();
        }
    }
}
