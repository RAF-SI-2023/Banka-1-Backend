package rs.edu.raf.banka1.dtos.employee;

public class CreateEmployeeDto {

    private String email;
    private String firstName;
    private String lastName;
    private String jmbg;
    private String position;
    private String phoneNumber;
    private Boolean active;
    private Double limitNow;
    private Double orderlimit;
    private Boolean requireApproval;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getJmbg() {
        return jmbg;
    }

    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Double getLimitNow() {
        return limitNow;
    }

    public void setLimitNow(Double limitNow) {
        this.limitNow = limitNow;
    }

    public Double getOrderlimit() {
        return orderlimit;
    }

    public void setOrderlimit(Double orderlimit) {
        this.orderlimit = orderlimit;
    }

    public Boolean getRequireApproval() {
        return requireApproval;
    }

    public void setRequireApproval(Boolean requireApproval) {
        this.requireApproval = requireApproval;
    }
}
