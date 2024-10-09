package fpt.edu.vn.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(nullable = false)
    public String url;

    public boolean isMain;

    public String publicId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore // Tránh ánh xạ ngược để tránh vòng lặp vô hạn
    public User user;
}
