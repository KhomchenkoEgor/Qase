package api.models.project;


import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectRq {

    @Expose
    private String title;
    @Expose
    private String code;
    @Expose
    private String description;
    @Expose
    private String access;
    @Expose
    private String group;
}
