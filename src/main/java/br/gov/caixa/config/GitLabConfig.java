package br.gov.caixa.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "gitlab")
public class GitLabConfig {
    
    private String url;
    private String token;
    private String groups;
    private String projects;
    private String users;
    private String usersPath;
    private String groupQualidade;
    /**
     * @return String return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return String return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }


    /**
     * @return String return the groups
     */
    public String getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(String groups) {
        this.groups = groups;
    }

    /**
     * @return String return the projects
     */
    public String getProjects() {
        return projects;
    }

    /**
     * @param projects the projects to set
     */
    public void setProjects(String projects) {
        this.projects = projects;
    }


    /**
     * @return String return the users
     */
    public String getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(String users) {
        this.users = users;
    }


    /**
     * @return String return the usersPath
     */
    public String getUsersPath() {
        return usersPath;
    }

    /**
     * @param usersPath the usersPath to set
     */
    public void setUsersPath(String usersPath) {
        this.usersPath = usersPath;
    }

    /**
     * @return String return the groupQualidade
     */
    public String getGroupQualidade() {
        return groupQualidade;
    }

    /**
     * @param groupQualidade the groupQualidade to set
     */
    public void setGroupQualidade(String groupQualidade) {
        this.groupQualidade = groupQualidade;
    }

}