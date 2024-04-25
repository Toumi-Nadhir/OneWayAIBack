package OneWayDev.tn.OneWayDev.Service;

import OneWayDev.tn.OneWayDev.Entity.Audio;
import OneWayDev.tn.OneWayDev.Repository.AudioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service

public class AudioService {

    private static final Logger log = LoggerFactory.getLogger(AudioService.class);

    private final WebClient webClient;

    private final AudioRepository audioRepository;

    public AudioService(WebClient.Builder webClientBuilder, AudioRepository audioRepository) {
        this.webClient = webClientBuilder.build();
        this.audioRepository = audioRepository;
    }

 public String transcribeAudio(String modelSTTUrl, Path audioFilePath) throws IOException {
    String filename = audioFilePath.getFileName().toString();
    Optional<Audio> existingModelSTT = audioRepository.findByFilename(filename);
    if (existingModelSTT.isPresent()) {
        log.info("Audio file {} already exists in the database. Returning the stored transcription.", filename);
        return existingModelSTT.get().getTranscription();
    } else {
        log.info("Audio file {} does not exist in the database. Transcribing and storing the audio file and transcription result.", filename);
        FileSystemResource fileSystemResource = new FileSystemResource(audioFilePath);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", fileSystemResource).filename(fileSystemResource.getFilename()).contentType(MediaType.parseMediaType("audio/wav"));

        Mono<String> responseMono = webClient.post()
                .uri(modelSTTUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class);

        String response = responseMono.block();

        Audio audio = new Audio();
        audio.setFilename(filename);
        audio.setAudioFile(Files.readAllBytes(audioFilePath));
        audio.setTranscription(response);
        audioRepository.save(audio);

        log.info("Transcription result for audio file {} has been stored in the database.", filename);

        return response;
    }
}
}