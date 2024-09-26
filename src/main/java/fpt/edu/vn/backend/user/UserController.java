package fpt.edu.vn.backend.user;

import fpt.edu.vn.backend.dto.ChangePasswordRequest;
import fpt.edu.vn.backend.dto.PhotoResponse;
import fpt.edu.vn.backend.entity.User;
import fpt.edu.vn.backend.service.UploadImageFile;
import fpt.edu.vn.backend.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final UploadImageFile uploadImageFile;

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("File size exceeds the limit of 10MB");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId = ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(uploadImageFile.uploadImage(file, userId));
    }
}
