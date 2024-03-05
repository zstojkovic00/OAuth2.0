package singidunum.rs.resourceserver.AccessToken;


public record Jwt(Header header, Payload payload, byte[] signedData, byte[] signature) {
}