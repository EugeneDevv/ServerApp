package com.example.thunderbolt_108.serverapp.Model;

public class Banner {
    private String id,name,image;

    public Banner() {

    }

    public Banner(String id,String name,String image) {
        this.id=id;
        this.name=name;
        this.image=image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}

