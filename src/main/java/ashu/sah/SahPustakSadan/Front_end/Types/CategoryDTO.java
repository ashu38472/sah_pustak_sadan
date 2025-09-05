package ashu.sah.SahPustakSadan.Front_end.Types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}