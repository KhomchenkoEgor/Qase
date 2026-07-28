package api.models.suite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuiteRq {
    @Expose
    private String title;

    @Expose
    private String description;

    @Expose
    private String preconditions;

    @SerializedName("parent_id")
    @Expose
    private Integer parentId;
}
