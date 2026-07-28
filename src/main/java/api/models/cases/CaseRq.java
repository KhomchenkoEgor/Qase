package api.models.cases;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CaseRq {

    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private String preconditions;
    @Expose
    private String postconditions;
    @Expose
    private Integer severity;
    @Expose
    private Integer priority;
    @Expose
    private Integer behavior;
    @Expose
    private Integer type;
    @Expose
    private Integer layer;

    @SerializedName("is_flaky")
    @Expose
    private Integer isFlaky;

    @SerializedName("isManual")
    @Expose
    private Integer isManual;

    @SerializedName("isToBeAutomated")
    @Expose
    private Integer isToBeAutomated;

    @Expose
    private Integer status;

    @SerializedName("steps_type")
    @Expose
    private String stepsType;

    @Expose
    private List<Step> steps;
}
