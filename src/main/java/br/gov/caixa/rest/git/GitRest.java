package br.gov.caixa.rest.git;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.poi.EncryptedDocumentException;

import br.gov.caixa.service.git.GitService;
import br.gov.caixa.util.Roles;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jose4j.json.internal.json_simple.parser.ParseException;

@RolesAllowed({ Roles.ADMIN })
@Path("/git")
public class GitRest {

    @Inject
    GitService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/greeting/{name}")
    public String greeting(@PathParam(value = "name") final String name) {
        return service.greeting(name);
    }

    @POST
    @Path("/cadastrarGrupoRepoExcel")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response cadastrarGrupoRepoExcel(MultipartFormDataInput input)
            throws Exception {
        InputStream stream = input.getFormDataPart("file", InputStream.class, null);
        File file = service.cadastrarGrupoRepoExcel(stream);
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment;filename=" + file);
        return response.build();
    }

    @POST
    @Path("/cadastrarUsuarioEmGruposExcel")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response cadastrarUsuarioEmGruposExcel(MultipartFormDataInput input)
            throws EncryptedDocumentException, IOException, ParseException {
        InputStream stream = input.getFormDataPart("file", InputStream.class, null);
        File file = service.cadastrarUsuarioEmGruposExcel(stream);
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment;filename=" + file);
        return response.build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }
}