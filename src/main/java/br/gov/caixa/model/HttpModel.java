package br.gov.caixa.model;

public class HttpModel {
    private String message;
    private Integer status;

    /**
     * @return String return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return Integer return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    public HttpModel() {
    }

    public HttpModel(String message, Integer status) {
        this.message = message;
        this.status = status;
    }

    public HttpModel message(String message) {
        this.message = message;
        return this;
    }

    public HttpModel status(Integer status) {
        this.status = status;
        return this;
    }


    @Override
    public String toString() {
        return "{" +
            " message='" + getMessage() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }

}