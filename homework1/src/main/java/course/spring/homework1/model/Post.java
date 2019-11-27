package course.spring.homework1.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Document("posts")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    private String id;
    private LocalDateTime published = LocalDateTime.now();
    @NonNull
    @NotNull
    @Size(min = 2, max = 60)
    private String title;
    @Size(min= 4)
    private String author;
    @NonNull
    @Size(min= 10, max = 4096)
    private String content;
    private List<String> tags;
    private String photo;
    private String status = "active";
}
