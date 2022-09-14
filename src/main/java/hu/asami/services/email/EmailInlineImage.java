package hu.asami.services.email;

import lombok.Data;
import org.springframework.core.io.InputStreamSource;

@Data
public class EmailInlineImage {
    private String name;
    private String type;
    private InputStreamSource data;
}