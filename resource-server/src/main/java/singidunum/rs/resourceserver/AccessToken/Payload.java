package singidunum.rs.resourceserver.AccessToken;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Payload(@JsonProperty("sub") String subject,
                      @JsonProperty("exp") Integer expiry,
                      @JsonProperty("scp") List<String> scopes) {
}
