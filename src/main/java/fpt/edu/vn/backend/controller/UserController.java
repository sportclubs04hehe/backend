package fpt.edu.vn.backend.controller;

import fpt.edu.vn.backend.dto.ChangePasswordRequest;
import fpt.edu.vn.backend.dto.MemberResponse;
import fpt.edu.vn.backend.entity.User;
import fpt.edu.vn.backend.service.UploadImageFile;
import fpt.edu.vn.backend.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "https://localhost:4200", allowCredentials = "true")
public class UserController {
    private final UserService userService;
    private final UploadImageFile uploadImageFile;

    @GetMapping("/get-all")
    public ResponseEntity<List<MemberResponse>> getUsersWithFilters(
            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "8") int pageSize,
            @RequestParam(defaultValue = "18") Integer minAge,
            @RequestParam(defaultValue = "99") Integer maxAge,
            @RequestParam(name = "gender", required = false) String gender,
            @RequestParam(defaultValue = "lastActive") String orderBy,
            @AuthenticationPrincipal User currentUser) {

        Page<MemberResponse> memberResponses = userService.getUsersWithFilters(currentUser.getId(), gender, minAge, maxAge, orderBy, pageNumber, pageSize);

        // Create a JSON-like string for pagination details
        String paginationDetails = String.format("{\"currentPage\":%d,\"itemsPerPage\":%d,\"totalItems\":%d,\"totalPages\":%d}",
                memberResponses.getNumber() + 1,
                memberResponses.getSize(),
                memberResponses.getTotalElements(),
                memberResponses.getTotalPages());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Pagination", paginationDetails); // Adding the pagination info to the headers

        return ResponseEntity.ok().headers(headers).body(memberResponses.getContent());
    }

    @GetMapping("/{username}")
    public ResponseEntity<MemberResponse> findUserByEmail(@PathVariable String username) {
        try {
            MemberResponse memberResponse = userService.findUserByEmail(username);
            if (memberResponse != null) {
                return ResponseEntity.ok(memberResponse);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error finding user by email: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File size exceeds the limit of 10MB");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return ResponseEntity.badRequest().body("Authentication failed");
            }

            int userId = ((User) authentication.getPrincipal()).getId();
            return ResponseEntity.ok(uploadImageFile.uploadImage(file, userId));
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.badRequest().body("Error uploading image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-image/{photoId}")
    public ResponseEntity<String> deleteImage(@PathVariable int photoId) {
        try {
            uploadImageFile.deletePhoto(photoId);
            return ResponseEntity.ok("Photo deleted successfully");
        } catch (RuntimeException | IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
