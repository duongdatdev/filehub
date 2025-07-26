package com.duongdat.filehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProjectSummary {
    private Long id;
    private String name;
    private String role;
}
