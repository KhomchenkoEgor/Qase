package api.models.cases;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Step {
    @Expose
    private String action;
    @Expose
    private String expected_result;
    @Expose
    private String data;
    @Expose
    private String value;
}
