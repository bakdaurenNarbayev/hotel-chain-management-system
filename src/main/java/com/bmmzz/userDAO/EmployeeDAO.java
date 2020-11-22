package com.bmmzz.userDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bmmzz.userDAO.struct.EmployeeInfo;
import com.bmmzz.userDAO.struct.EmployeeRegistrationInfo;
import com.bmmzz.userDAO.struct.EmployeeSchedulesInfo;
import com.bmmzz.userDAO.struct.EmployeesForAdmin;
import com.google.gson.Gson;

public class EmployeeDAO {
	
	private EmployeeDAO() {}

	public static void addEmployee(EmployeeRegistrationInfo employee) {
		if(UserDAO.userExists(employee.getLogin(), "employee"))
			return;
		
		int employeeID = 1; 
		if( UserDAO.executeQueryINT("SELECT COUNT(*) FROM mydb.employee") > 0 ) {
			Database db = null;
			Connection connection = null;
			PreparedStatement ps = null;
			ResultSet resultSet = null;
			
			try {
				db = new Database();
				connection = db.getConnection();
				
				ps = connection.prepareStatement("SELECT EmployeeID FROM mydb.employee ORDER BY EmployeeID DESC LIMIT 1;");
				resultSet = ps.executeQuery(); 
				resultSet.next();
				
				employeeID = resultSet.getInt(1) + 1;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try { resultSet.close(); } catch (Exception e) {}
				try { ps.close(); } catch (Exception e) {}
				try { connection.close(); } catch (Exception e) {}
			}
		}	
		
		UserDAO.executeUpdate("INSERT INTO mydb.employee VALUES (" + employeeID +", '" + employee.getFullName() + "', '" + employee.getGender() + "', "
				+ "'" + employee.getDateOfBirth() +"', '" + employee.getIdentificationType() + "', '" + employee.getIdentificationNumber() + "', "
				+ "'" + employee.getCitizenship() + "', '" + employee.getVisa() + "', '" + employee.getAddress() + "', '" + employee.getBankCardNumber() + "', "
				+ "'" + employee.getEmailAddress() + "', '" + employee.getHomePhoneNumber() + "', '" + employee.getMobilePhoneNumber() + "', '" + employee.getLogin() + "', "
				+ "'" + employee.getPassword() + "')");
		UserDAO.executeUpdate("INSERT INTO mydb.schedule VALUES (" + employeeID + ", " + employee.getHotelID() + ", '" + employee.getPosition() + "', "
				+ "'" + employee.getStatus() + "', '" + employee.getPayRate() + "', '" + employee.getStartDate() + "', " + employee.getEndDate() + ", '" + employee.getStartTime() + "', '" + employee.getEndTime() + "')");
	}
	
	public static String getEmployeeInfo(String auth) {
		Gson gson = new Gson();
		EmployeeInfo employeeInfo = new EmployeeInfo();
		String json = "";
		
		String username = UserDAO.getDecodedAuth(auth)[0];
		
		Database db = null;
		Connection connection = null;
		PreparedStatement ps = null, ps2 = null, ps3 = null;
		ResultSet resultSet = null, resultSet2 = null, resultSet3 = null;
		
		try {
			db = new Database();
			connection = db.getConnection();
			
			ps = connection.prepareStatement("SELECT * FROM mydb.employee WHERE Login= BINARY '" + username + "'" );
			resultSet = ps.executeQuery(); 
			
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
			
			ps2 = connection.prepareStatement("SELECT * FROM mydb.schedule WHERE EmployeeID= BINARY " + employeeInfo.getEmployeeID());
			resultSet2 = ps2.executeQuery(); 
			
			resultSet2.next();
			employeeInfo.setHotelID( resultSet2.getInt(2) );
			employeeInfo.setPosition( resultSet2.getString(3) );
			employeeInfo.setStatus( resultSet2.getString(4) );
			employeeInfo.setPayrate( resultSet2.getString(5) );
			employeeInfo.setStartDate( resultSet2.getDate(6).toString() );
			if(resultSet2.getDate(7) == null)
				employeeInfo.setEndDate("NULL");
			else
				employeeInfo.setEndDate( resultSet2.getDate(7).toString() );
			employeeInfo.setStartTime( resultSet2.getString(8) );
			employeeInfo.setEndTime( resultSet2.getString(9) );
			
			ps3 = connection.prepareStatement("SELECT Name FROM mydb.hotel WHERE HotelID= BINARY " + employeeInfo.getHotelID() );
			resultSet3 = ps3.executeQuery(); 
			resultSet3.next();
			employeeInfo.setHotelName( resultSet3.getString(1) );
			
			json = gson.toJson(employeeInfo, EmployeeInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try { resultSet.close(); } catch (Exception e) {}
			try { resultSet2.close(); } catch (Exception e) {}
			try { resultSet3.close(); } catch (Exception e) {}
			try { ps.close(); } catch (Exception e) {}
			try { ps2.close(); } catch (Exception e) {}
			try { ps3.close(); } catch (Exception e) {}
			try { connection.close(); } catch (Exception e) {}
		}
		
		return json;
	}
	
	public static int getHotelID(String auth) {
		String username = UserDAO.getDecodedAuth(auth)[0];
		
		Database db = null;
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		
		try {
			db = new Database();
			connection = db.getConnection();
			
			ps = connection.prepareStatement("Select mydb.schedule.hotelID From mydb.schedule, mydb.employee Where Login= BINARY '" + username + "' and mydb.schedule.employeeid = mydb.employee.EmployeeID");
			resultSet = ps.executeQuery(); 
			
			resultSet.next();
			return resultSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try { resultSet.close(); } catch (Exception e) {}
			try { ps.close(); } catch (Exception e) {}
			try { connection.close(); } catch (Exception e) {}
		}
		return -1;
	}
	
	public static void editEmployeeSchedule(String auth, int employeeID, String startTime, String endTime) {
		UserDAO.executeUpdate("Update mydb.schedule " + 
							"Set starttime='" + startTime + "', endtime='" + endTime + "' " +
							"Where employeeID='" + employeeID + "' and hotelid='" + getHotelID(auth) + "' and position<>'Manager'");
	}
	
	public static void editEmployeePayRate(String auth, int employeeID, String payRate) {
		UserDAO.executeUpdate("Update mydb.schedule " + 
							"Set PayRate='" + payRate + "' " +
							"Where employeeID='" + employeeID + "' and hotelid='" + getHotelID(auth) + "' and position<>'Manager'");
	}
	
	public static String getSchedules(String auth) {
		Gson gson = new Gson();
		EmployeeSchedulesInfo es = new EmployeeSchedulesInfo();
		String json = "";
		int hID = EmployeeDAO.getHotelID(auth);
		
		Database db = null;
		Connection connection = null;
		PreparedStatement ps = null, ps2 = null, ps3 = null;
		ResultSet rES = null, numDays = null, workingDays = null;
		
		try {
			//get es for this hotel
			db = new Database();
			connection = db.getConnection();
			
			ps = connection.prepareStatement("Select * From mydb.employee E, mydb.schedule S " +
					"Where S.HotelID='" + hID + "' and " +
					"E.EmployeeID=S.EmployeeID and S.Position<>'Manager';");
			rES = ps.executeQuery();
			
			while(rES.next()) {
				Date startTime = new Date();
				Date endTime = new Date();
				try {
					startTime = new SimpleDateFormat("HH:mm:ss").parse(rES.getString(23));
					endTime = new SimpleDateFormat("HH:mm:ss").parse(rES.getString(24));				
				} catch (ParseException e) {
					e.printStackTrace();
				} 
				long minLong = (endTime.getTime() - startTime.getTime()) / (60 * 1000);
				double hoursDb = (double) minLong/60;
				double hours = Math.round(hoursDb * 100.0) / 100.0;
				
				//checking for right hours
				int startH = Integer.parseInt(rES.getString(23).substring(0, 2));
				int endH = Integer.parseInt(rES.getString(24).substring(0, 2));
				
				if(endH < startH) hours += 24;
				double hourlyWage = Double.parseDouble(rES.getString(20).substring(1, rES.getString(20).length()));
				double dailySalDb = hours * hourlyWage;

				//get numWorkingDays/Week
				ps2 = connection.prepareStatement("SELECT count(*) " +
						   "FROM mydb.day_of_the_week D " +
						   "Where D.EmployeeID='" + rES.getInt(1)+ "' and D.HotelID='" + rES.getInt(17) + "';");
				numDays = ps2.executeQuery(); 
				
				double weeklySalDb = 0;
				if(numDays.next()) {
					weeklySalDb = dailySalDb * numDays.getInt(1);
				}

				String cur = String.valueOf(rES.getString(20).charAt(0));
				String dailySal = cur + String.valueOf(dailySalDb);
				String weeklySal = cur + String.valueOf(weeklySalDb);
				
				ps3 = connection.prepareStatement("SELECT D.Day_of_the_Week " +
							 "FROM mydb.day_of_the_week D " +
							 "Where D.EmployeeID='" + rES.getInt(1)+ "' and D.HotelID='" + rES.getInt(17) + "';");
				workingDays = ps3.executeQuery(); 

				String wDays = new String();
				while(workingDays.next()) {
					wDays += workingDays.getString(1);
				}
				
				es.addES(rES.getInt(1), rES.getString(2), rES.getString(11), rES.getString(13), rES.getInt(17), rES.getString(18), rES.getString(20), rES.getString(21), rES.getString(23), rES.getString(24), dailySal, weeklySal, wDays);
			}
			json = gson.toJson(es, EmployeeSchedulesInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try { rES.close(); } catch (Exception e) {}
			try { numDays.close(); } catch (Exception e) {}
			try { workingDays.close(); } catch (Exception e) {}
			try { ps.close(); } catch (Exception e) {}
			try { ps2.close(); } catch (Exception e) {}
			try { ps3.close(); } catch (Exception e) {}
			try { connection.close(); } catch (Exception e) {}
		}
		return json;
	}

	public static String getEmployees() {
		Gson gson = new Gson();
		EmployeesForAdmin efa = new EmployeesForAdmin();
		String json = "";
		
		Database db = null;
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			db = new Database();
			connection = db.getConnection();
			
			ps = connection.prepareStatement("Select * from mydb.employee E, mydb.schedule S, mydb.hotel H " +
					"Where E.EmployeeID=S.EmployeeID and S.HotelID=H.HotelID Order by H.HotelID;");
			rs = ps.executeQuery(); 
			
			while(rs.next()) {
				/*String todayStr = java.time.LocalDate.now().toString();
				Date todayDate = new Date();
				Date endDate = new Date();
				try {
					todayDate = new SimpleDateFormat("yyyy-mm-dd").parse(todayStr);
					if(!rs.getString(22).equals("null")) {
						endDate = new SimpleDateFormat("yyyy-mm-dd").parse(rs.getString(22));
					}
				} catch (ParseException e1) {
					e1.printStackTrace();
				}*/
				//if(rs.getString(22).equals("null") || todayDate.before(endDate)) {
				efa.addEmpFA(rs.getInt(17), rs.getString(26), rs.getInt(1), rs.getString(2), rs.getString(18), rs.getString(11), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(21), rs.getString(22));
				//}
			}
			json = gson.toJson(efa, EmployeesForAdmin.class);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try { rs.close(); } catch (Exception e) {}
			try { ps.close(); } catch (Exception e) {}
			try { connection.close(); } catch (Exception e) {}
		}
		return json;
	}
	
	public static void deleteEmployee(int employeeID) {
		UserDAO.executeUpdate("Delete from mydb.day_of_the_week Where employeeid='" + employeeID + "'");
		UserDAO.executeUpdate("Delete from mydb.schedule Where employeeid='" + employeeID + "'");
		UserDAO.executeUpdate("Delete from mydb.employee Where employeeid='" + employeeID + "'");
	}
}


