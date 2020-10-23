package com.bmmzz.userDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.regex.Pattern;

import com.google.gson.Gson;

public class EmployeeDAO {
	
	private EmployeeDAO() {}

	public static void addEmployee(EmployeeRegistrationInfo employee) {
		if(UserDAO.userExists(employee.login, "employee"))
			return;

		int employeeID = 1; 
		if( UserDAO.executeQueryINT("SELECT COUNT(*) FROM mydb.employee") > 0 ) {
			try {
				ResultSet resultSet = UserDAO.executeQuery("SELECT EmployeeID FROM mydb.employee ORDER BY EmployeeID DESC LIMIT 1;");
				resultSet.next();
				employeeID = resultSet.getInt(1) + 1;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
		
		UserDAO.executeUpdate("INSERT INTO mydb.employee VALUES (" + employeeID +", '" + employee.fullName + "', '" + employee.gender + "', "
				+ "'" + employee.dateOfBirth +"', '" + employee.identificationType + "', '" + employee.identificationNumber + "', "
				+ "'" + employee.citizenship + "', '" + employee.visa + "', '" + employee.address + "', '" + employee.bankCardNumber + "', "
				+ "'" + employee.emailAddress + "', '" + employee.homePhoneNumber + "', '" + employee.mobilePhoneNumber + "', '" + employee.login + "', "
				+ "'" + employee.password + "')");
		UserDAO.executeUpdate("INSERT INTO mydb.employee_at_hotel VALUES (" + employeeID + ", " + employee.hotelID + ", '" + employee.getPosition() + "', "
				+ "'" + employee.status + "', '" + employee.payRate + "', '" + employee.startDate + "', " + employee.getEndDate() + ")");
	}
	
	public static String getEmployeeInfo(String auth) {
		Gson gson = new Gson();
		EmployeeInfo employeeInfo = new EmployeeInfo();
		String json = "";
		
		byte[] decodedBytes = Base64.getDecoder().decode(auth);
		String decodedAuth = new String(decodedBytes);
		
		if( !Pattern.compile(".+:.+").matcher(decodedAuth).matches() )
			return null;
		
		String username = decodedAuth.split(":", 2)[0];
		
		try {
			ResultSet resultSet = UserDAO.executeQuery("SELECT * FROM mydb.employee WHERE Login= BINARY '" + username + "'" );
			resultSet.next();
			employeeInfo.setEmployeeID( resultSet.getInt(1) );
			employeeInfo.setFullName( resultSet.getString(2) );
			employeeInfo.setGender( resultSet.getString(3) );
			employeeInfo.setDateOfBirth( resultSet.getDate(4).toString() );
			employeeInfo.setIdentificationType( resultSet.getString(5) );
			employeeInfo.setIdentificationNumber( resultSet.getString(6) );
			employeeInfo.setCitizenShip( resultSet.getString(7) );
			employeeInfo.setVise( resultSet.getString(8) );
			employeeInfo.setAddress( resultSet.getString(9) );
			employeeInfo.setBankCardNumber( resultSet.getString(10) );
			employeeInfo.setEmailAdress( resultSet.getString(11) );
			employeeInfo.setHomePhoneNumber( resultSet.getString(12) );
			employeeInfo.setMobilePhoneNumber( resultSet.getString(13) );
			
			resultSet = UserDAO.executeQuery("SELECT * FROM mydb.employee_at_hotel WHERE EmployeeID= BINARY " + employeeInfo.getEmployeeID());
			resultSet.next();
			employeeInfo.setHotelID( resultSet.getInt(2) );
			employeeInfo.setPosition( resultSet.getString(3) );
			employeeInfo.setStatus( resultSet.getString(4) );
			employeeInfo.setPayrate( resultSet.getString(5) );
			employeeInfo.setStartDate( resultSet.getDate(6).toString() );
			if(resultSet.getDate(7) == null)
				employeeInfo.setEndDate("NULL");
			else
				employeeInfo.setEndDate( resultSet.getDate(7).toString() );
			
			resultSet = UserDAO.executeQuery("SELECT Name FROM mydb.hotel WHERE HotelID= BINARY " + employeeInfo.getHotelID() );
			resultSet.next();
			employeeInfo.setHotelName( resultSet.getString(1) );
			
			json = gson.toJson(employeeInfo, EmployeeInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
}