package com.poulogy.site.configuration;

import com.poulogy.site.modules.PostType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    // белый список расширений
    private static final Map<String, PostType> ALLOWED = Map.of(
            "jpg", PostType.IMAGE, "jpeg", PostType.IMAGE, "png", PostType.IMAGE,
            "gif", PostType.IMAGE, "webp", PostType.IMAGE,
            "mp4", PostType.VIDEO, "webm", PostType.VIDEO, "mov", PostType.VIDEO);

    private final Path dir;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.dir = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);
    }

    public Path getDir() { return dir; }

    public record Stored(String fileName, PostType kind) {}

    public Stored store(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw bad("Файл пустой");
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        PostType kind = ext == null ? null : ALLOWED.get(ext.toLowerCase(Locale.ROOT));
        if (kind == null) throw bad("Разрешены фото (jpg, png, gif, webp) и видео (mp4, webm, mov)");
        String name = UUID.randomUUID() + "." + ext.toLowerCase(Locale.ROOT);
        try (var in = file.getInputStream()) {
            Files.copy(in, dir.resolve(name));
        }
        return new Stored(name, kind);
    }

    public void delete(String fileName) {
        try { Files.deleteIfExists(dir.resolve(fileName).normalize()); } catch (IOException ignored) {}
    }

    private static ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}

