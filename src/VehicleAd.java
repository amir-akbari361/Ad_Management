public class VehicleAd extends Ad {
    private int year;
    private int mileage;
    private boolean accidentStatus;

    public VehicleAd(String title, String description, double price, String category, String owner, String phone, String imagePath, int year, int mileage, boolean accidentStatus) {
        super(title, description, price, category, owner, phone, imagePath);
        this.year = year;
        this.mileage = mileage;
        this.accidentStatus = accidentStatus;
    }
    public void edit(String title, String description, double price, String phone,String imagePath, int year, int mileage, boolean accidentStatus) {
        super.edit(title,description,price,phone,imagePath);
        this.year = year;
        this.mileage = mileage;
        this.accidentStatus=accidentStatus;
    }
    public int getYear(){
        return year;
    }
    public int getMileage(){
        return mileage;
    }
    public boolean isAccidented(){
        return accidentStatus;
    }
    @Override
    public String toString() {
        return super.toString() + "\nYear: " + year + "\nMileage: " + mileage + "\nAccident Status: " + (accidentStatus ? "Yes" : "No");
    }
}
