package com.bmmzz.userDAO.struct;

public class EmployeeInfo {
	private int EmployeeID;
	private String FullName;
	private String Gender;
	private String DateOfBirth;
	private String IdentificationType;
	private String IdentificationNumber;
	private String CitizenShip;
	private String Vise;
	private String Address;
	private String BankCardNumber;
	private String EmailAdress;
	private String HomePhoneNumber;
	private String MobilePhoneNumber;
	private String Position;
	private String Status;
	private String Payrate;
	private String StartDate;
	private String EndDate;
	private String StartTime;
	private String EndTime;
	private int HotelID;
	private String HotelName;
	
	public int getEmployeeID() {
		return EmployeeID;
	}
	public void setEmployeeID(int employeeID) {
		EmployeeID = employeeID;
	}
	public String getFullName() {
		return FullName;
	}
	public void setFullName(String fullName) {
		FullName = fullName;
	}
	public String getGender() {
		return Gender;
	}
	public void setGender(String gender) {
		Gender = gender;
	}
	public String getDateOfBirth() {
		return DateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		DateOfBirth = dateOfBirth;
	}
	public String getIdentificationType() {
		return IdentificationType;
	}
	public void setIdentificationType(String identificationType) {
		IdentificationType = identificationType;
	}
	public String getIdentificationNumber() {
		return IdentificationNumber;
	}
	public void setIdentificationNumber(String identificationNumber) {
		IdentificationNumber = identificationNumber;
	}
	public String getCitizenShip() {
		return CitizenShip;
	}
	public void setCitizenShip(String citizenShip) {
		CitizenShip = citizenShip;
	}
	public String getVise() {
		return Vise;
	}
	public void setVise(String vise) {
		Vise = vise;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getBankCardNumber() {
		return BankCardNumber;
	}
	public void setBankCardNumber(String bankCardNumber) {
		BankCardNumber = bankCardNumber;
	}
	public String getEmailAdress() {
		return EmailAdress;
	}
	public void setEmailAdress(String emailAdress) {
		EmailAdress = emailAdress;
	}
	public String getHomePhoneNumber() {
		return HomePhoneNumber;
	}
	public void setHomePhoneNumber(String homePhoneNumber) {
		HomePhoneNumber = homePhoneNumber;
	}
	public String getMobilePhoneNumber() {
		return MobilePhoneNumber;
	}
	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		MobilePhoneNumber = mobilePhoneNumber;
	}
	public String getPosition() {
		return Position;
	}
	public void setPosition(String position) {
		Position = position;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getPayrate() {
		return Payrate;
	}
	public void setPayrate(String payrate) {
		Payrate = payrate;
	}
	public String getStartDate() {
		if(StartDate == null)
			return "09:00:00";
		return StartDate;
	}
	public void setStartDate(String startDate) {
		StartDate = startDate;
	}
	public String getEndDate() {
		if(EndDate == null)
			return "18:00:00";
		return EndDate;
	}
	public void setEndDate(String endDate) {
		EndDate = endDate;
	}
	
	public String getStartTime() {
		return StartTime;
	}
	public void setStartTime(String startTime) {
		StartTime = startTime;
	}
	public String getEndTime() {
		return EndTime;
	}
	public void setEndTime(String endTime) {
		EndTime = endTime;
	}
	public int getHotelID() {
		return HotelID;
	}
	public void setHotelID(int hotelID) {
		HotelID = hotelID;
	}
	
	
	public String getHotelName() {
		return HotelName;
	}
	public void setHotelName(String hotelName) {
		HotelName = hotelName;
	}
}

