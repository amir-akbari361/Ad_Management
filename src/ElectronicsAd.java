public class ElectronicsAd extends Ad {
    private String model;
    private String color;
    private boolean trade;
    public ElectronicsAd(String title, String description, double price, String category, String owner, String phone, String imagePath, String model, String color, boolean trade){
        super(title,description,price,category,owner,phone,imagePath);
        this.model=model;
        this.color=color;
        this.trade=trade;
    }
    public void edit(String title, String description, double price, String phone,String imagePath, String model,String color, boolean trade) {
        super.edit(title,description,price,phone,imagePath);
        this.model=model;
        this.color=color;
        this.trade=trade;
    }


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isTrade() {
        return trade;
    }

    public void setTrade(boolean trade) {
        this.trade = trade;
    }

    @Override
    public String toString() {
        return super.toString() + "\nModel: " + model + "\nColor: " + color + "\nWhnt to trade: " + (trade ? "Yes" : "No");
    }
}
