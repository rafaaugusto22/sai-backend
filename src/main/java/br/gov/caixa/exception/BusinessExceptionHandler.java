package br.gov.caixa.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
 
@Provider
public class BusinessExceptionHandler implements ExceptionMapper<BusinessException> 
{
    @Override
    public Response toResponse(BusinessException exception) 
    {
        return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();  
    }
}