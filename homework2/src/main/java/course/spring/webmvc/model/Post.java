package course.spring.webmvc.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Document("posts")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    private String id;
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime published = LocalDateTime.now();
    @NonNull
    private User author;
    @NonNull
    @NotNull
    @Size(min = 2, max = 60)
    private String title;
    @NonNull
    @NotNull
    @Size(min = 5, max = 4096)
    private String content;
    private String tags;
    private String pictureUrl;
    @NonNull
    private String status = "active";
}
