package fpt.edu.vn.backend.config;

import fpt.edu.vn.backend.dto.MemberResponse;
import fpt.edu.vn.backend.dto.PhotoResponse;
import fpt.edu.vn.backend.entity.Photo;
import fpt.edu.vn.backend.entity.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.stream.Collectors;

public class UserMapper {
    public static MemberResponse toMemberResponse(User user) {
        MemberResponse memberResponse = new MemberResponse();

        memberResponse.setId(user.getId());
        memberResponse.setFirstName(user.getFirstName());
        memberResponse.setLastName(user.getLastName());
        memberResponse.setKnowAs(user.getKnowAs());
        memberResponse.setEmail(user.getUsername());
        memberResponse.setDateCreated(user.getDateCreated());
        memberResponse.setLastActive(user.getLastActive());
        memberResponse.setGender(user.getGender());
        memberResponse.setIntroduction(user.getIntroduction());
        memberResponse.setInterests(user.getInterests());
        memberResponse.setLookingFor(user.getLookingFor());
        memberResponse.setCity(user.getCity());
        memberResponse.setCountry(user.getCountry());

        if (user.getDateOfBirth() != null) {
            int age = Period.between(user.getDateOfBirth(), LocalDate.now()).getYears();
            memberResponse.setAge(age);
        }

        String photoUrl = user.getPhotos().stream()
                .filter(Photo::isMain)
                .findFirst()
                .map(Photo::getUrl)
                .orElse(null);
        memberResponse.setPhotoUrl(photoUrl);

        memberResponse.setPhotos(user.getPhotos().stream()
                .map(photo -> new PhotoResponse(photo.getId(), photo.getUrl(), photo.isMain()))
                .collect(Collectors.toList()));

        return memberResponse;
    }
}
