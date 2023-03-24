package br.gov.caixa.exception;

import java.io.Serializable;

public class BusinessException extends Exception implements Serializable
{
    private static final long serialVersionUID = 1L;
 
    public BusinessException() {
        super();
    }
    public BusinessException(String msg)   {
        super(msg);
    }
    public BusinessException(String msg, Exception e)  {
        super(msg, e);
    }
}