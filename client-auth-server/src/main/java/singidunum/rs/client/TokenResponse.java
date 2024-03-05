package singidunum.rs.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(@JsonProperty("id_token") String idToken,
                            @JsonProperty("access_token") String accessToken) {
}