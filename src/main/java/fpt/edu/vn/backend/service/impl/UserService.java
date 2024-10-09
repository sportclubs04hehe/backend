package fpt.edu.vn.backend.service.impl;

import fpt.edu.vn.backend.config.UserMapper;
import fpt.edu.vn.backend.dto.ChangePasswordRequest;
import fpt.edu.vn.backend.dto.MemberResponse;
import fpt.edu.vn.backend.entity.User;
import fpt.edu.vn.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public Page<MemberResponse> getUsersWithFilters(Integer userId, String gender, Integer ageFrom, Integer ageTo, String orderBy, int page, int size) {
        Pageable pageable;

        // Sắp xếp theo `dateCreated` hoặc `lastActive`
        if ("dateCreated".equals(orderBy)) {
            pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "dateCreated"));
        } else {
            pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastActive"));
        }

        // Chuyển gender thành chữ thường để không phân biệt chữ hoa/chữ thường
        if (gender != null) {
            gender = gender.toLowerCase();
        }

        // Tính toán khoảng ngày sinh dựa trên tuổi
        if (ageFrom != null && ageTo != null) {
            LocalDate currentDate = LocalDate.now();
            LocalDate startDate = currentDate.minusYears(ageTo); // Ngày sinh của người lớn nhất
            LocalDate endDate = currentDate.minusYears(ageFrom); // Ngày sinh của người nhỏ tuổi nhất

            // Lọc theo giới tính và ngày sinh (tuổi)
            return userRepository.findUsersByGenderAndAgeRangeAndExcludeCurrentUser(gender, userId, startDate, endDate, pageable)
                    .map(UserMapper::toMemberResponse);
        } else {
            // Nếu không có lọc theo tuổi, chỉ lọc theo giới tính
            return userRepository.findUsersByGenderAndExcludeCurrentUser(gender, userId, pageable)
                    .map(UserMapper::toMemberResponse);
        }
    }

    public MemberResponse findUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return UserMapper.toMemberResponse(user);
        } else {
            return null; // or throw an exception, depending on your requirements
        }
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPass(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        userRepository.save(user);
    }


}
