package OneWayDev.tn.OneWayDev.Controller;

import OneWayDev.tn.OneWayDev.Service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/model-stt")
public class AudioController {

    private final AudioService audioService;

    @Autowired
    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }

   @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public String transcribeAudio(@RequestParam("file") MultipartFile file, @RequestParam("url") String url) {
    try {
        Path path = Paths.get(file.getOriginalFilename());
        file.transferTo(path);
        String transcription = audioService.transcribeAudio(url, path);
        path.toFile().delete();
        return transcription;
    } catch (Exception e) {
        throw new RuntimeException("Failed to transcribe audio", e);
    }
}
}