package OneWayDev.tn.OneWayDev.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomErrorResponse {
    private int status;
    private String error;

    public CustomErrorResponse(int status, String error) {
        this.status = status;
        this.error = error;
    }
}