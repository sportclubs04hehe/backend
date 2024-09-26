package fpt.edu.vn.backend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import fpt.edu.vn.backend.dto.PhotoResponse;
import fpt.edu.vn.backend.entity.Photo;
import fpt.edu.vn.backend.entity.User;
import fpt.edu.vn.backend.repository.PhotoRepository;
import fpt.edu.vn.backend.repository.UserRepository;
import fpt.edu.vn.backend.service.UploadImageFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadFileImageImpl implements UploadImageFile {

    private final Cloudinary cloudinary;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;

    @Override
    public PhotoResponse uploadImage(MultipartFile file, int userId) throws IOException {
        assert file.getOriginalFilename() != null;
        String publicValue = generatePublicValue(file.getOriginalFilename());
        String extension = getFileName(file.getOriginalFilename())[1];
        File fileUpload = convert(file);
        Map<String, String> uploadResult = cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap("public_id", publicValue));
        cleanDisk(fileUpload);
        String url = uploadResult.get("url");

        Photo photo = Photo.builder()
                .url(url)
                .isMain(false)
                .publicId(publicValue)
                .user(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
                .build();

        photo = photoRepository.save(photo);

        return PhotoResponse.builder()
                .id(photo.getId())
                .url(photo.getUrl())
                .isMain(photo.isMain())
                .build();
    }

    @Override
    public void deletePhoto(int photoId) throws IOException {
        Photo photo = photoRepository.findById(photoId).orElseThrow(() -> new RuntimeException("Photo not found"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId = ((User) authentication.getPrincipal()).getId();

        if (photo.getUser().getId() != userId) {
            throw new RuntimeException("You do not have permission to delete this photo");
        }

        cloudinary.uploader().destroy(photo.getPublicId(), ObjectUtils.emptyMap());
        photoRepository.delete(photo);
    }

    private File convert(MultipartFile file) throws IOException {
        assert file.getOriginalFilename() != null;
        File convFile = new File(StringUtils.join(generatePublicValue(file.getOriginalFilename()), getFileName(file.getOriginalFilename())[1]));
        try(InputStream is = file.getInputStream()) {
            Files.copy(is, convFile.toPath());
        }
        return convFile;
    }

    private void cleanDisk(File file) {
        try {
            Path filePath = file.toPath();
            Files.delete(filePath);
        } catch (IOException e) {
            log.error("Error");
        }
    }

    public String generatePublicValue(String originalName){
        String fileName = getFileName(originalName)[0];
        return StringUtils.join(UUID.randomUUID().toString(), "_", fileName);
    }

    public String[] getFileName(String originalName) {
        return originalName.split("\\.");
    }
}
