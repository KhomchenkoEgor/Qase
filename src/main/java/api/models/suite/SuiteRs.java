package api.models.suite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SuiteRs {
    @SerializedName("status")
    @Expose
    public Boolean status;

    @SerializedName("result")
    @Expose
    public SuiteResult result;
}
