package com.example.SR_REST.controllers;

import lombok.Getter;
import lombok.Setter;

public class CountryInfo {
    private String name;
    private double population, area, c19deaths, c19infections, d, ratio;
    private String examplePhotoUrl;
    public CountryInfo(String name) {
        this.name = name;
    }

    public void setData(double population, double area){
        this.population = population;
        this.area = area;
        this.d = population/area;
    }
    public void setC19Data(double c19infections, double c19deaths){
        this.c19deaths = c19deaths;
        this.c19infections = c19infections;
    }
    public void setRatio(){
        this.ratio = this.c19infections/this.population;
    }

    public String getName() {
        return name;
    }

    public double getPopulation() {
        return population;
    }

    public double getArea() {
        return area;
    }

    public String getExamplePhotoUrl() {
        return examplePhotoUrl;
    }

    public double getC19deaths() {
        return c19deaths;
    }

    public double getC19infections() {
        return c19infections;
    }

    public double getD() {
        return d;
    }

    public double getRatio() {
        return ratio;
    }

    public void setExamplePhotoUrl(String examplePhotoUrl) {
        this.examplePhotoUrl = examplePhotoUrl;
    }

}
