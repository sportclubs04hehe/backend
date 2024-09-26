package fpt.edu.vn.backend.dto;

import lombok.Builder;

@Builder
public class PhotoResponse {
    public int id;
    public String url;
    public boolean isMain;
}
