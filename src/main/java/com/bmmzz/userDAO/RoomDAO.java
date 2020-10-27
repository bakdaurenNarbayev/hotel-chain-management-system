package com.bmmzz.userDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.bmmzz.userDAO.struct.AvailableRoomsInfo;
import com.google.gson.Gson;

public class RoomDAO {
	public static void reserveRoomType(String roomTypeName, int hotelID, String auth, String checkInDate, String checkOutDate, int numberOfRooms) {
		int guestID = UserDAO.getGuestID(auth);
		ResultSet resultSet = UserDAO.executeQuery("SELECT NumberOfRooms FROM mydb.reserves WHERE "
				+ "RoomTypeName = BINARY '" + roomTypeName + "' AND HotelID = " + hotelID + " AND GuestID = " + guestID + " "
				+ "AND CheckInDate = '" + checkInDate + "' AND CheckOutDate = '" + checkOutDate + "';");
		try {
			if(resultSet.next()) {
				numberOfRooms += resultSet.getInt(1);
				UserDAO.executeUpdate("UPDATE mydb.reserves SET NumberOfRooms=" + numberOfRooms + " WHERE "
						+ "RoomTypeName = BINARY '" + roomTypeName + "' AND HotelID = " + hotelID + " AND GuestID = " + guestID + " "
						+ "AND CheckInDate = '" + checkInDate + "' AND CheckOutDate = '" + checkOutDate + "';");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		UserDAO.executeUpdate("INSERT INTO mydb.reserves VALUES "
				+ "('" + roomTypeName + "', " + hotelID + ", " + guestID + ", "
				+ "'" + checkInDate + "', '" + checkOutDate + "', " + numberOfRooms + ");");
	}
	
	public static String getAvailableRoomsInfo(int hotelID, String startDateStr, String endDateStr) {
		Gson gson = new Gson();
		String json = "";
		
		try {
			Date startDate = new SimpleDateFormat("yyyy:MM:dd").parse(startDateStr);
			Date endDate = new SimpleDateFormat("yyyy:MM:dd").parse(endDateStr);
			
			AvailableRoomsInfo availableRoomsInfo = new AvailableRoomsInfo();
			
			ResultSet resultSet = UserDAO.executeQuery("Select TypeName, Size, Capacity FROM mydb.room_type WHERE HotelID = " + hotelID + ";");
			while(resultSet.next()) {
				int availableRoomNum = getNumberOfAvailableRooms( hotelID, startDate, endDate, resultSet.getString(1) );
				if(availableRoomNum <= 0) {continue;}
				
				double initialPrice = getInitialPrice(hotelID, startDate, endDate, resultSet.getString(1) );
				
				availableRoomsInfo.add(resultSet.getString(1), resultSet.getDouble(2), resultSet.getInt(3), initialPrice, availableRoomNum);
			}
			
			json = gson.toJson(availableRoomsInfo, AvailableRoomsInfo.class);
		} catch (ParseException | SQLException e1) {
			e1.printStackTrace();
		}
		
		return json;
	}
	
	private static double getInitialPrice(int hotelID, Date startDate, Date endDate, String typeName) {
		Double totalPrice = 0.0;
		try {
			Date date = new Date(startDate.getTime());
			
			// Acquiring price for each day separately and summing them
			while( date.compareTo(endDate) <= 0 ) {
				String seasonName = "";
				char dayOfTheWeek = getDayOfTheWeek(date);
				
				ResultSet resultSet = UserDAO.executeQuery("SELECT * FROM mydb.time_period WHERE "
						+ "DayOfTheWeek= '" + dayOfTheWeek + "';");
				
				// Finding the corresponding season of the specific day (startDay)
				while(resultSet.next()) {
					Date seasonStartDate = new SimpleDateFormat("yyyy-MM-dd").parse(resultSet.getString(3));
					Date seasonEndDate = new SimpleDateFormat("yyyy-MM-dd").parse(resultSet.getString(4));
					if(seasonStartDate.compareTo(date) <= 0 && seasonEndDate.compareTo(date) >= 0) {
						seasonName = resultSet.getString(2);
						break;
					}
				}
				
				resultSet = UserDAO.executeQuery("SELECT Amount FROM mydb.initial_price WHERE "
						+ "RoomTypeName = BINARY '" + typeName + "' AND HotelID = " + hotelID + " AND "
						+ "DayOfTheWeek = BINARY '" + dayOfTheWeek + "' AND SeasonName = BINARY '" + seasonName +"';");
				
				resultSet.next();
				totalPrice += resultSet.getDouble(1);
				date = new Date(date.getTime() + 86400000);
				
			}
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return totalPrice;
	}
	
	private static char getDayOfTheWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);
		switch(dayOfTheWeek) {
			case 1:
				return 'H';
			case 2:
				return 'M';
			case 3:
				return 'T';
			case 4:
				return 'W';
			case 5:
				return 'R';
			case 6:
				return 'F';
			case 7:
				return 'S';
			default:
				return 'E';
		}
	}
	
	private static int getNumberOfAvailableRooms(int hotelID, Date startDate, Date endDate, String typeName) {
		try {
			// Getting the number of rooms in a given room type at given hotel.
			ResultSet resultSet = UserDAO.executeQuery("SELECT NumberOfRooms "
					+ "FROM  mydb.room_type WHERE TypeName = BINARY '" + typeName + "' AND HotelID = "+ hotelID + ";");
			resultSet.next();
			int maxNumOfRooms = resultSet.getInt(1);
			
			// Creating an array to keep track of the number of available rooms in a specific day.
			int numOfDays = getDifferenceInDays(startDate, endDate);
			int availableRoomsNumOnDay[] = new int[numOfDays];
			for(int i = 0; i < numOfDays; i++) {
				availableRoomsNumOnDay[i] =  maxNumOfRooms;
			}
			
			// Counting number of available rooms for each day in the given time interval.
			resultSet = UserDAO.executeQuery("SELECT CheckInDate, CheckOutDate, NumberOfRooms "
				+ "FROM mydb.reserves WHERE RoomTypeName = BINARY '" + typeName + "' AND HotelID = " + hotelID + ";");
			while(resultSet.next()) {		
				Date reservedStartDate = new SimpleDateFormat("yyyy-MM-dd").parse(resultSet.getString(1));
				Date reservedEndDate = new SimpleDateFormat("yyyy-MM-dd").parse( resultSet.getString(2));
				int numOfReservedRooms = resultSet.getInt(3);
				
				if(startDate.after(reservedEndDate) || endDate.before(reservedStartDate)) {continue;}
				
				Date fromDate = reservedStartDate.after(startDate) ? reservedStartDate : startDate;
				Date toDate = reservedEndDate.before(endDate) ? reservedEndDate : endDate;
				
				for(int i = getDifferenceInDays(startDate, fromDate)-1 ; i < getDifferenceInDays(startDate, toDate); i++ ) {
					availableRoomsNumOnDay[i] -= numOfReservedRooms;
					if(availableRoomsNumOnDay[i] <= 0) {return 0;}
				}
			}
			
			// Counting maximum number of rooms that can be reserved in the given time interval.
			int min = maxNumOfRooms;
			for(int i = 0; i < numOfDays; i++) {
				if(availableRoomsNumOnDay[i] < min)
					min = availableRoomsNumOnDay[i];
			}
			return min;
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	// get difference between to dates (inclusive)
	private static int getDifferenceInDays(Date startDate, Date endDate) {
		return Math.round((endDate.getTime() - startDate.getTime()) / 86400000) + 1;
	}
}
