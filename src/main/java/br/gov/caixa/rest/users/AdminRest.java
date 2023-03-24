package br.gov.caixa.rest.users;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.gov.caixa.util.Roles;

import javax.annotation.security.RolesAllowed;

import io.quarkus.security.Authenticated;

@Path("/admin")
@RolesAllowed({Roles.ADMIN}) 
@Authenticated
public class AdminRest {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String admin() {
        return "granted";
    }
}