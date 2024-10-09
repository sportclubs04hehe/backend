package fpt.edu.vn.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponse {
    public int id;
    public String url;
    public boolean isMain;
}
