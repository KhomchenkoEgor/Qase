package api.models.plan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PlanResult {
    @SerializedName("id")
    @Expose
    public Integer id;
}
