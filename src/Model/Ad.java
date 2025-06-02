package Model;

import java.io.Serializable;

public class Ad implements Serializable{
    private int id;
    private String title;
    private String description;
    private double price;
    private String category;
    private String owner;
    private String phone;
    private String imagePath;

    public Ad(int id,String title, String description, double price, String category, String owner,  String phone, String imagePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.owner = owner;
        this.phone = phone;
        this.imagePath = imagePath;
    }
    public Ad(String title, String description, double price, String category, String owner, String phone, String imagePath) {
        this(-1, title, description, price, category, owner, phone, imagePath);
    }

    public int getId()
    {
        return id;
    }
    public String getOwner()
    {
        return owner;
    }
    public String getImagePath()
    {
        return imagePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getCategory(){
        return category;
    }
    public double getPrice(){
        return price;
    }
    public String getDescription(){
        return description;
    }

    public String getPhone(){
        return phone;
    }

    public String getTitle(){
        return title;
    }


    public void edit(String title, String description, double price, String phone, String imagePath) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.phone = phone;
        this.imagePath = imagePath;
    }
    @Override
    public String toString() {
        return "Title: " + title + "\nDescription: " + description + "\nPrice: " + price + "\nCategory: " + category + "\nPhone: " + phone + (imagePath != null ? "\nImage: " + imagePath : "");
    }
}


