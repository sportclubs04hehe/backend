package fpt.edu.vn.backend.entity;

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
    public User user;
}
