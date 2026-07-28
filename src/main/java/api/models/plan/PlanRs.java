package api.models.plan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PlanRs {
    @SerializedName("status")
    @Expose
    public Boolean status;

    @SerializedName("result")
    @Expose
    public PlanResult result;
}
