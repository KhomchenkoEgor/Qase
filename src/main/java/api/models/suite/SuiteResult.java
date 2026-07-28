package api.models.suite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SuiteResult {
    @SerializedName("id")
    @Expose
    public Integer id;
}
