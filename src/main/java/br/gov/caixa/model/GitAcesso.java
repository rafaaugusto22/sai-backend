package br.gov.caixa.model;

public class GitAcesso {
    private String grupo;
    private String usuario;
    private Integer nuAcesso;

    /**
     * @return String return the grupo
     */
    public String getGrupo() {
        return grupo;
    }

    /**
     * @param grupo the grupo to set
     */
    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    /**
     * @return String return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return Integer return the nuAcesso
     */
    public Integer getNuAcesso() {
        return nuAcesso;
    }

    /**
     * @param nuAcesso the nuAcesso to set
     */
    public void setNuAcesso(Integer nuAcesso) {
        this.nuAcesso = nuAcesso;
    }

}