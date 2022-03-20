package com.example.SR_REST.controllers;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: server
@Controller
@Slf4j
public class FormController {

    RestTemplate restTemplate;
    final String COUNTRY_DATA_API_PATH = "https://restcountries.com/v3.1/name";
    final String C19_DATA_API_PATH = "https://covid-19-coronavirus-statistics.p.rapidapi.com/v1/total";
    final String EXAMPLE_IMG_API_PATH = "https://api.unsplash.com/search/photos?page=1&per_page=1&query=";
    public CountryInfo country1, country2;

    public FormController() {
        this.restTemplate = new RestTemplate();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String form(){
        return "form";
    }

    @RequestMapping(value = "/formpost", method = RequestMethod.POST)
    public String getUserData(@RequestParam String country1, @RequestParam String country2){

        this.country1 = new CountryInfo(country1);
        this.country2 = new CountryInfo(country2);

        boolean ok1 = true, ok2 = true;
        ok1 = ok1 && getCountryData(this.country1);
        ok2 = ok2 && getCountryData(this.country2);
        if(ok1 ) {
            ok1 = ok1 && getCovidData(this.country1);
        }
        if(ok2){
            ok2 = ok2 && getCovidData(this.country2);
        }
        if(ok1) {
            getExamplePhoto(this.country1);
        }
        if(ok2){
            getExamplePhoto(this.country2);
        }
        if(!ok1 && !ok2){
            return "redirect:/";
        }
        return "redirect:/results";
    }
    @RequestMapping(value = "/results", method = RequestMethod.GET)
    public String getResults(Model model){
        Map<String, CountryInfo> map = new HashMap<>();
        map.put("country1", country1);
        map.put("country2", country2);
        model.addAllAttributes(map);
        return "results";
    }

    private boolean getExamplePhoto(CountryInfo country){
        HttpEntity<String> request;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Client-ID 3694gLJ2S2cHk0LbwyilBsbfxsfdrr8AEGP2SoapXHw");

        request = new HttpEntity<>(headers);
        try{
            ResponseEntity<String> response = restTemplate.exchange(
                    EXAMPLE_IMG_API_PATH + country.getName().toLowerCase(),
                    HttpMethod.GET,
                    request,
                    String.class,
                    1
            );
            if(response != null && response.getStatusCode().value() != 200){
                return false;
            }else if(response != null){
                String jsonString = response.getBody().toString();
                JSONObject jsonpObject = new JSONObject(jsonString)
                        .getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONObject("urls");
                country.setExamplePhotoUrl(jsonpObject.getString("small"));
            }else{
                return false;
            }
        }catch (RestClientException e){
            System.out.println("Check Internet connection");
            return false;
        }

        return true;
    }



    private boolean getCovidData(CountryInfo country){
        HttpEntity<String> request;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("x-rapidapi-host", "covid-19-coronavirus-statistics.p.rapidapi.com");
        headers.set("x-rapidapi-key", "9a6da0ef45msh4e3e0ab88f03b96p18125fjsnd35f85888954");
        String countryName = country.getName().toLowerCase().substring(0, 1).toUpperCase() +
                country.getName().substring(1).toLowerCase();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(C19_DATA_API_PATH).queryParam("country", countryName);
        request = new HttpEntity<>(headers);
        Map<String, String> params = new HashMap<>();
        params.put("country", countryName);
        ResponseEntity<String> response = null;

        try{
             response = restTemplate.exchange(
                    builder.buildAndExpand(params).toUri(),
                    HttpMethod.GET,
                    request,
                    String.class

            );
            if(response != null && response.getStatusCode().value() != 200){
                return false;
            }else if(response != null){
                String jsonString = response.getBody().toString();
                JSONObject data = new JSONObject(jsonString).getJSONObject("data");
                country.setC19Data(data.getInt("confirmed"), data.getInt("deaths"));
                country.setRatio();
            }else{
                return false;
            }
        }catch (RestClientException e){
            System.out.println("Check Internet connection");
            return false;
        }

        return true;
    }

    private boolean getCountryData(CountryInfo country){
        try {
            ResponseEntity response = this.restTemplate
                    .getForEntity(COUNTRY_DATA_API_PATH + "/" + country.getName().toLowerCase(), String.class);
            if(response != null && response.getStatusCode().value() != 200){
                return false;
            }else if(response != null ){
                String jsonString = response.getBody().toString();
                JSONObject jsonpObject = new JSONArray(jsonString).getJSONObject(0);
                country.setData(new Double(jsonpObject.get("population").toString()),
                        new Double(jsonpObject.get("area").toString()));
            }else{
                return false;
            }
        }catch(RestClientException e){
            System.out.println("Check your internet connection");
            return false;
        }
        return true;
    }

}
