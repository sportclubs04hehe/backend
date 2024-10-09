package fpt.edu.vn.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    public Integer id;
    public String firstName;
    public String lastName;
    public String email;
    public LocalDateTime dateCreated;
    public LocalDateTime lastActive;
    public Integer age;
    public String photoUrl;
    public String knowAs;
    public String gender;
    public String introduction;
    public String interests;
    public String lookingFor;
    public String city;
    public String country;
    public List<PhotoResponse> photos;
}
