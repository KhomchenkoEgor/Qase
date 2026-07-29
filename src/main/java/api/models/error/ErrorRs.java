package api.models.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorRs {

    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
}
