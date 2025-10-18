/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.finance.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bhaumikramani
 */
public class RestClient<T,V> {
    private String url;
    private RestTemplate restTemplate;
    private HttpMethod type;
    private int retryCount;
    private int retryDelayInMS;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RestClient.class);

    public RestClient(String url, HttpMethod type, int retryCount, int retryDelayInMS) {
        this.url = url;
        this.restTemplate = new RestTemplate();
        this.type = type;
        this.retryCount = retryCount;
        this.retryDelayInMS = retryDelayInMS;
    }
    
    public RestClient(String url, HttpMethod type) {
        this(url, type, 0, 0);
    }
    
    public ResponseEntity<V> execute(T request, Class<V> responseClass){
        return execute(request, responseClass, getHeaders());
    }
    
    public ResponseEntity<V> execute(T request, Class<V> responseClass, HttpHeaders headers){
        HttpEntity<T> requestEntity = new HttpEntity<>(request, headers);
        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<V> response = null;
        try {
            String jsonResponse = mapper.writeValueAsString(request);
            System.out.println("EInvoice Response :: "+jsonResponse);
            logger.info("Restclient response : {}", jsonResponse);
            response = this.restTemplate.exchange(this.url, this.type, requestEntity, responseClass);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }
    
    
    
    private HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        return headers;
    }
}
