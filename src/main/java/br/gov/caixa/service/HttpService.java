package br.gov.caixa.service;

import java.io.IOException;
import java.util.List;

import javax.enterprise.context.RequestScoped;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import br.gov.caixa.model.HttpModel;

@RequestScoped
public class HttpService {

    public  HttpModel sendPOST(final String url, final List<Header>  headers, final List<NameValuePair> urlParameters)
            throws IOException {
        
        HttpModel httpObj = new HttpModel();
        String result = "";
        final HttpPost post = new HttpPost(url);
        
        for(final Header h :headers){
            post.setHeader(h);
        }
        
        // send a JSON data
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(post)) {

            result = EntityUtils.toString(response.getEntity());
            httpObj.setMessage(result);
            httpObj.setStatus(response.getStatusLine().getStatusCode());
        }

        return httpObj;
    }

    public HttpModel sendGET(final String url, final List<Header>  headers) throws IOException {

        final CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpModel httpObj = new HttpModel();
        String result = "";
        try {

            final HttpGet request = new HttpGet(url);

            // add request headers
            for(final Header h :headers){
                request.setHeader(h);
            }

            final CloseableHttpResponse response = httpClient.execute(request);

            try {

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                }
                httpObj.setMessage(result);
                httpObj.setStatus(response.getStatusLine().getStatusCode());

            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }

    return httpObj;
    }

    public HttpModel sendPUT(final String url, final List<Header>  headers) throws IOException {

        final CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpModel httpObj = new HttpModel();
        String result = "";
        try {

            final HttpPut request = new HttpPut(url);

            // add request headers
            for(final Header h :headers){
                request.setHeader(h);
            }

            final CloseableHttpResponse response = httpClient.execute(request);

            try {

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                }
                httpObj.setMessage(result);
                httpObj.setStatus(response.getStatusLine().getStatusCode());


            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }

    return httpObj;
    }
    
}