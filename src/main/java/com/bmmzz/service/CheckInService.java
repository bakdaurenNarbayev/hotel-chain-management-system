package com.bmmzz.service;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bmmzz.userDAO.EmployeeDAO;
import com.bmmzz.userDAO.HotelDAO;
import com.bmmzz.userDAO.RoomDAO;
import com.bmmzz.userDAO.UserDAO;

@Path("/checkin")
public class CheckInService {

	@Context ServletContext servletContext;
	
	public CheckInService() {
		UserDAO.connectToUserDAO();
	}
	
	@GET
    @Produces({MediaType.TEXT_HTML})
    public InputStream get( @DefaultValue("") @QueryParam("auth") String auth ) {
        if(!UserDAO.checkAuth(auth))
            return Helper.getPage(servletContext, "accessDeniedPage.html");
        switch( UserDAO.getRole(auth) ) {
            case "desk-clerk":
                return Helper.getPage(servletContext, "checkInPage.html");
            default:
                return Helper.getPage(servletContext, "accessDeniedPage.html");
        }
    }
	
	@GET
	@Path("/rooms")
	public Response hotelRoomsInfo( @DefaultValue("") @QueryParam("auth") String auth ) {
		if(!UserDAO.checkRoleAndAuth(auth, "desk-clerk"))
			return null;
		String json = HotelDAO.getHotelRooms(auth);
		return Response.ok(json).build();
	}
	
	@POST
	@Path("/{guestID}-{roomTypeName}-{roomNumber}-{roomFloor}-{checkInDate}-{checkOutDate}")
	public Response checkIn( @DefaultValue("") 	@QueryParam("auth") String auth,
												@PathParam("checkInDate") String checkInDate,
												@PathParam("checkOutDate") String checkOutDate,
												@PathParam("roomTypeName") String roomTypeName,
												@PathParam("roomNumber") String roomNumber,
												@PathParam("roomFloor") int roomFloor,
												@PathParam("guestID") int guestID) {
		
		if (!UserDAO.checkRoleAndAuth(auth, "desk-clerk"))
			return null;	
		checkInDate = checkInDate.replace(':', '-');
		checkOutDate = checkOutDate.replace(':', '-');
		
		//hotelId and roomTypeName should be added after DB updating
		RoomDAO.checkInRoom(checkInDate, checkOutDate, roomNumber, roomFloor, guestID);
		
		HotelDAO.setRoomOccupied(Integer.parseInt(roomNumber), 1);
		HotelDAO.setGuestInRoom(Integer.parseInt(roomNumber), guestID);
		HotelDAO.setCID(Integer.parseInt(roomNumber), checkInDate);
		HotelDAO.setCOD(Integer.parseInt(roomNumber), checkOutDate);
		
		return Response.ok().build();
	}
}
