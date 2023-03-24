package br.gov.caixa.service.git;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import br.gov.caixa.config.GitLabConfig;
import br.gov.caixa.enums.GitAcessoEnum;
import br.gov.caixa.model.GitAcesso;
import br.gov.caixa.model.GitRepositorio;
import br.gov.caixa.model.HttpModel;
import br.gov.caixa.service.HttpService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@RequestScoped
public class GitService {

    private static final String RESPONSE_TEST = "[{\"id\":2,\"web_url\":\"http://localhost/groups/SISOU\",\"name\":\"SISOU\",\"path\":\"SISOU\"},{\"id\":3,\"web_url\":\"http://localhost/groups/SISOU\",\"name\":\"SISTA\",\"path\":\"SISTA\"}]";


    private static final Logger LOGGER = Logger.getLogger(GitService.class.getName());

    private final List<String> lines = new ArrayList<>();

    @Inject
    HttpService http;

    @Inject
    GitLabConfig gitLabConfig;

    public String greeting(final String name) {
        return "hello " + name;
    }

    public File cadastrarGrupoRepoExcel(final InputStream inputstream) throws Exception {

        final List<GitRepositorio> listaRepo = recuperarGrupoRepoExcel(inputstream);
        final JsonArray jsonArrFull = recuperaGruposFullGitLab();
        for (final GitRepositorio repo : listaRepo) {
            System.out.println("Criando repositório. Grupo: " + repo.getGrupo() + ", Projeto: " + repo.getProjeto());
            lines.add("Criando repositório. Grupo: " + repo.getGrupo() + ", Projeto: " + repo.getProjeto());
            criarGrupoProjetoGitLab(repo, jsonArrFull);
        }

        
        final Path p = Files.write(Files.createTempFile("log",".txt"), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
        p.toFile().deleteOnExit();
        return p.toFile();

    }

    public File cadastrarUsuarioEmGruposExcel(final InputStream inputstream) throws EncryptedDocumentException, IOException {

        final List<GitAcesso> listaUsu = recuperarUsuarioGrupoExcel(inputstream);
        final JsonArray jsonArrFull = recuperaGruposFullGitLab();
        for (final GitAcesso acesso : listaUsu) {
            System.out.println("Adicionando Usuário no Grupo. Grupo: " + acesso.getGrupo() + ", Usuário: " + acesso.getUsuario() + ", Role: " + acesso.getNuAcesso());
            lines.add("Adicionando Usuário no Grupo. Grupo: " + acesso.getGrupo() + ", Usuário: " + acesso.getUsuario() + ", Role: " + acesso.getNuAcesso());
            addUsuarioGrupoGitLab(acesso, jsonArrFull);
        }

        
        final Path p = Files.write(Files.createTempFile("log",".txt"), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
        p.toFile().deleteOnExit();
        return p.toFile();

    }

    private List<GitRepositorio> recuperarGrupoRepoExcel(final InputStream inputstream)
            throws Exception {

        final List<GitRepositorio> lista = new ArrayList<>();

        // Creating a Workbook from an Excel file (.xls or .xlsx)
        final Workbook workbook = WorkbookFactory.create(inputstream);

        // Retrieving the number of sheets in the Workbook
        LOGGER.info("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
        /*
         * ============================================================= Iterating over
         * all the sheets in the workbook (Multiple ways)
         * =============================================================
         */

        LOGGER.info("Retrieving Sheets");
        workbook.forEach(sheet -> {
            LOGGER.info("=> " + sheet.getSheetName());
        });

        /*
         * ================================================================== Iterating
         * over all the rows and columns in a Sheet (Multiple ways)
         * ==================================================================
         */

        // Getting the Sheet at index zero
        final Sheet sheet = workbook.getSheetAt(0);

        // Create a DataFormatter to format and get each cell's value as String
        final DataFormatter dataFormatter = new DataFormatter();

        final Integer lastCell = 1;
        LOGGER.info("\n\nIterating over Rows and Columns\n");
        if (sheet.getRow(0).getCell(0).getStringCellValue().toUpperCase().matches("GRUPO|GRUPOS")
                && sheet.getRow(0).getCell(1).getStringCellValue().toUpperCase().matches("PROJETO|PROJETOS")){
            for (final Row row : sheet) {
                if (row.getRowNum() != 0) {
                    GitRepositorio gitRepo = null;
                    for (final Cell cell : row) {
                        final String cellValue = dataFormatter.formatCellValue(cell).trim();
                        System.out.print(cellValue + "\t");

                        if (cell.getColumnIndex() > lastCell || (cellValue== null || cellValue.equals(""))){
                            break;
                        }
                        
                       
                        if (cell.getColumnIndex() == 0){
                            gitRepo = new GitRepositorio();
                            gitRepo.setGrupo(cellValue.toUpperCase());
                        }
                        //Ultima celula lida     
                        if (cell.getColumnIndex() == lastCell){
                            gitRepo.setProjeto(cellValue);
                            lista.add(gitRepo);
                        }
                               
                    }
                    System.out.println();
                }
            }
        }else{
            System.out.println("Arquivo não formatado corretamente");
            lines.add("Arquivo não formatado corretamente");
        }
        // Closing the workbook
        workbook.close();

        return lista;

    }


    private List<GitAcesso> recuperarUsuarioGrupoExcel(final InputStream inputstream)
            throws EncryptedDocumentException, IOException {

        final List<GitAcesso> lista = new ArrayList<>();

        // Creating a Workbook from an Excel file (.xls or .xlsx)
        final Workbook workbook = WorkbookFactory.create(inputstream);

        // Retrieving the number of sheets in the Workbook
        LOGGER.info("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
        /*
         * ============================================================= Iterating over
         * all the sheets in the workbook (Multiple ways)
         * =============================================================
         */

        LOGGER.info("Retrieving Sheets");
        workbook.forEach(sheet -> {
            LOGGER.info("=> " + sheet.getSheetName());
        });

        /*
         * ================================================================== Iterating
         * over all the rows and columns in a Sheet (Multiple ways)
         * ==================================================================
         */

        // Getting the Sheet at index zero
        final Sheet sheet = workbook.getSheetAt(0);

        // Create a DataFormatter to format and get each cell's value as String
        final DataFormatter dataFormatter = new DataFormatter();

        final Integer lastCell = 2;
        LOGGER.info("\n\nIterating over Rows and Columns\n");
        if (sheet.getRow(0).getCell(0).getStringCellValue().toUpperCase().matches("GRUPO|GRUPOS")
                && sheet.getRow(0).getCell(1).getStringCellValue().toUpperCase().matches("USUARIO|USUARIOS")
                && sheet.getRow(0).getCell(2).getStringCellValue().toUpperCase().matches("ROLE|ROLES")){
            for (final Row row : sheet) {
                if (row.getRowNum() != 0) {
                    GitAcesso gitAcesso = null;
                    for (final Cell cell : row) {
                        final String cellValue = dataFormatter.formatCellValue(cell).trim();
                        

                        if (cell.getColumnIndex() > lastCell || (cellValue== null || cellValue.equals(""))){
                            break;
                        }
                        
                        System.out.print(cellValue + "\t");
                       
                        if (cell.getColumnIndex() == 0){
                            gitAcesso = new GitAcesso();
                            gitAcesso.setGrupo(cellValue.toUpperCase());
                        }
                        if (cell.getColumnIndex() == 1){
                            gitAcesso.setUsuario(cellValue.toUpperCase());
                        }
                        //Ultima celula lida     
                        if (cell.getColumnIndex() == lastCell){
                            switch (cellValue.toUpperCase()) {
                                case "NO ACCESS":
                                    gitAcesso.setNuAcesso(GitAcessoEnum.NO.getCodigo());
                                    break;
                                case "GUEST ACCESS":
                                    gitAcesso.setNuAcesso(GitAcessoEnum.GUEST.getCodigo());
                                    break;
                                case "REPORTER ACCESS":
                                    gitAcesso.setNuAcesso(GitAcessoEnum.REPORTER.getCodigo());
                                    break;
                                case "DEVELOPER ACCESS":
                                    gitAcesso.setNuAcesso(GitAcessoEnum.DEVELOPER.getCodigo());
                                    break;
                                case "MAINTAINER ACCESS":
                                    gitAcesso.setNuAcesso(GitAcessoEnum.MANTEINER.getCodigo()); 
                                    break;
                                case "OWNER ACCESS":
                                    gitAcesso.setNuAcesso(GitAcessoEnum.OWNER.getCodigo()); 
                                    break;
                                default:
                                    gitAcesso.setNuAcesso(GitAcessoEnum.NO.getCodigo());
                                    break;
                            }
                            
                            lista.add(gitAcesso);
                        }
                               
                    }
                    System.out.println();
                }
            }
        }else{
            System.out.println("Arquivo não formatado corretamente");
            lines.add("Arquivo não formatado corretamente");
        }
        // Closing the workbook
        workbook.close();

        return lista;

    }

    private void criarGrupoProjetoGitLab(final GitRepositorio repo,JsonArray jsonArrFull) throws IOException {
        final List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("PRIVATE-TOKEN", gitLabConfig.getToken()));
        List<JsonObject> list = StreamSupport.stream(jsonArrFull.spliterator(), false).map(val -> (JsonObject) val)
                .filter(val -> val.getString("path").toUpperCase().equals(repo.getGrupo()))
                .collect(Collectors.toList());
        Integer groupId;
        if(!list.isEmpty()){
            groupId = list.get(0).getInteger("id");
            criarProjetoGitLab(repo.getProjeto(),groupId);
            //Criar Projeto
        }else{
            //CRIAR GRUPO se existir add json no jsonArrFull
           
                final JsonArray  jsonArray =criarGrupoGitLab(repo.getGrupo());
                if(jsonArray.toString().contains("Failed") && jsonArray.toString().contains("path")){
                    jsonArrFull = recuperaGruposFullGitLab();
                    list = StreamSupport.stream(jsonArrFull.spliterator(), false).map(val -> (JsonObject) val)
                    .filter(val -> val.getString("path").toUpperCase().equals(repo.getGrupo())).collect(Collectors.toList());
                    groupId = list.get(0).getInteger("id");
                }else{
                    final JsonObject json = (JsonObject) jsonArray.getValue(0);
                    jsonArrFull.add(json);
                    groupId = json.getInteger("id");
                }
                criarProjetoGitLab (repo.getProjeto(),groupId);
        }

    }

    private void addUsuarioGrupoGitLab(final GitAcesso acesso ,final JsonArray jsonArrFull) throws IOException {
        final List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("PRIVATE-TOKEN", gitLabConfig.getToken()));
        final List<JsonObject> listGrupo = StreamSupport.stream(jsonArrFull.spliterator(), false).map(val -> (JsonObject) val)
                .filter(val -> val.getString("path").toUpperCase().equals(acesso.getGrupo()))
                .collect(Collectors.toList());
        final JsonArray jsonArrUsuario = recuperaUsuarioGitLab(acesso.getUsuario());
        final List<JsonObject> listUsuario = StreamSupport.stream(jsonArrUsuario.spliterator(), false).map(val -> (JsonObject) val)
                .filter(val -> val.getString("username").toUpperCase().equals(acesso.getUsuario()))
                .collect(Collectors.toList());
        
        if(!listGrupo.isEmpty() && !listUsuario.isEmpty()){
            final Integer groupId = listGrupo.get(0).getInteger("id");
            final Integer userId = listUsuario.get(0).getInteger("id");
            final HttpModel httpModel =addUsuario(groupId,userId,acesso.getNuAcesso());
            
            if(httpModel.getStatus()==409){
                updateUsuario(groupId, userId, acesso.getNuAcesso());
            }
        }else if(listGrupo.isEmpty()){
            System.out.println("Grupo não encontrado. Grupo: " + acesso.getGrupo());
            lines.add("Grupo não encontrado. Grupo: " + acesso.getGrupo());
        }
        else if(listUsuario.isEmpty()){
            System.out.println("Usuário não encontrado. Usuário: " + acesso.getUsuario());
            lines.add("Usuário não encontrado. Usuário: " + acesso.getUsuario());
        }

    }

    private JsonArray criarGrupoGitLab(final String grupo) throws IOException {
        
        final List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("PRIVATE-TOKEN", gitLabConfig.getToken()));
        
        final List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("name", grupo));
        urlParameters.add(new BasicNameValuePair("path", grupo));
        
        final String url = gitLabConfig.getUrl() + gitLabConfig.getGroups();
        String response = http.sendPOST(url, headers,urlParameters).getMessage();
        response = response.startsWith("[")?response:"["+response+"]";
       
        final JsonArray jsonArr = new JsonArray(response);

        System.out.println("Criando grupo "+grupo+". Retorno: "+jsonArr.toString());    
        lines.add("Criando grupo "+grupo+". Retorno: "+jsonArr.toString());

        return jsonArr;

    }

    private void criarProjetoGitLab(final String projeto, final Integer groupId) throws IOException {
        final List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("PRIVATE-TOKEN", gitLabConfig.getToken()));
        final List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("name", projeto));
        urlParameters.add(new BasicNameValuePair("path", projeto));
        urlParameters.add(new BasicNameValuePair("namespace_id", String.valueOf(groupId) ));
        
        final String url = gitLabConfig.getUrl() + gitLabConfig.getProjects();
        final HttpModel httpModel = http.sendPOST(url, headers,urlParameters);
        String response = httpModel.getMessage();
        String responseArr = response.startsWith("[")?response:"["+response+"]";
        
        final JsonArray jsonArr = new JsonArray(responseArr);
        System.out.println("Criando projeto "+projeto+". Retorno: "+jsonArr.toString());
        lines.add("Criando projeto "+projeto+". Status: "+httpModel.getStatus()+". Retorno: "+jsonArr.toString());
        
        if(httpModel.getStatus().toString().startsWith("20")){
            JsonObject obj = new JsonObject(response);
            String projectId =  obj.getInteger("id").toString();
            addGroupQualidadeBR(projectId);
        }
    }

    private void addGroupQualidadeBR(final String projectId) throws IOException {
        final List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("PRIVATE-TOKEN", gitLabConfig.getToken()));
        final List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("group_id", gitLabConfig.getGroupQualidade() ));
        urlParameters.add(new BasicNameValuePair("group_access", "40"));
        
        final String url = gitLabConfig.getUrl() + gitLabConfig.getProjects()+"/"+String.valueOf(projectId)+"/share";
        final HttpModel httpModel = http.sendPOST(url, headers,urlParameters);
        String response = httpModel.getMessage();
        response = response.startsWith("[")?response:"["+response+"]";
        final JsonArray jsonArr = new JsonArray(response);
        System.out.println("Add group Qualidade BR . Retorno: "+jsonArr.toString());
        lines.add("Add group Qualidade BR . Retorno: "+jsonArr.toString());
    }

    private HttpModel addUsuario(final Integer grupo,final Integer usuario, final Integer nuAcesso) throws IOException {
        
        final List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("PRIVATE-TOKEN", gitLabConfig.getToken()));
        
        final List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("user_id", usuario.toString()));
        urlParameters.add(new BasicNameValuePair("access_level", nuAcesso.toString()));
        
        final String url = gitLabConfig.getUrl() + gitLabConfig.getGroups()+"/"+grupo+"/members";
        final HttpModel httpModel = http.sendPOST(url, headers,urlParameters);
        System.out.println("Add usuário "+usuario+". Retorno: "+httpModel.getMessage());    
        lines.add("Add usuário "+usuario+". Retorno: "+httpModel.getMessage());
        
        return httpModel;

    }

    private void updateUsuario(final Integer grupo,final Integer usuario, final Integer nuAcesso) throws IOException {
        
        final List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("PRIVATE-TOKEN", gitLabConfig.getToken()));
        
        final String url = gitLabConfig.getUrl() + gitLabConfig.getGroups()+"/"+grupo+"/members/"+usuario+"?access_level="+nuAcesso.toString();
        final HttpModel httpModel = http.sendPUT(url, headers);

        System.out.println("Atualizando usuário "+usuario+". Retorno: "+httpModel.getMessage());    
        lines.add("Atualizando usuário "+usuario+". Retorno: "+httpModel.getMessage());

    }

    private JsonArray recuperaGruposFullGitLab() throws IOException {
        int page = 1;
        final JsonArray jsonArrFull = new JsonArray();
        final List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("PRIVATE-TOKEN", gitLabConfig.getToken()));
        while (true) {

            final String url = gitLabConfig.getUrl() + gitLabConfig.getGroups() + "/?per_page=100&page=" + page;
            page++;
            final String response = http.sendGET(url, headers).getMessage();
            final JsonArray jsonArr = new JsonArray(response);
            if (jsonArr.isEmpty())
                break;
                for (int i = 0; i < jsonArr.size(); i++) {
                    jsonArrFull.add(jsonArr.getJsonObject(i));
                }
        }
       
        return jsonArrFull;

    }

    private JsonArray recuperaUsuarioGitLab(final String user) throws IOException {
        
        final JsonArray jsonArrFull = new JsonArray();
        final List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("PRIVATE-TOKEN", gitLabConfig.getToken()));

            final MessageFormat mf = new MessageFormat(gitLabConfig.getUsersPath());
            Object[] objArray = {user};
            final String url = gitLabConfig.getUrl() + gitLabConfig.getUsers() + mf.format(objArray);
            
            final String response = http.sendGET(url, headers).getMessage();
            final JsonArray jsonArr = new JsonArray(response);
            
            for (int i = 0; i < jsonArr.size(); i++) {
                jsonArrFull.add(jsonArr.getJsonObject(i));
            }
        
       
        return jsonArrFull;

    }

    public static void main(String[] args) {
        String texto= "{\"id\":5619}";
        JsonObject obj = new JsonObject(texto);
        System.out.println(obj.getInteger("id"));
       
    }
}