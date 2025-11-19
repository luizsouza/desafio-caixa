package br.gov.caixa.painelinvestimentos.model.dto;

public class AuthResponseDTO {

    private String token;
    private String tipo = "Bearer";

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
