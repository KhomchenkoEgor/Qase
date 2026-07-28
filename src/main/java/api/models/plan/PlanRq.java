package api.models.plan;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PlanRq {
    @Expose
    private String title;

    @Expose
    private String description;

    @Expose
    private List<Integer> cases;
}
