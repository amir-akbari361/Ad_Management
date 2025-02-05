import java.io.Serializable;

public class Ad implements Serializable {
    private String title;
    private String description;
    private double price;
    private String category;
    private String owner;
    private String phone;
    private String imagePath;

    public Ad(String title, String description, double price, String category, String owner,  String phone, String imagePath) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.owner = owner;
        this.phone = phone;
        this.imagePath = imagePath;
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


