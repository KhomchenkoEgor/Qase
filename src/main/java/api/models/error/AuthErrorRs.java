package api.models.error;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AuthErrorRs {

    @SerializedName("error")
    private String error;
}
