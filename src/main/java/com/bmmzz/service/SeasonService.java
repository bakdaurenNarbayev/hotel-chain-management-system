package com.bmmzz.service;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
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
import com.bmmzz.userDAO.SeasonDAO;
import com.bmmzz.userDAO.UserDAO;

@Path("/season")
public class SeasonService {

	@Context ServletContext servletContext;
	
	public SeasonService() {
		UserDAO.connectToUserDAO();
	}
	
	@GET
    @Produces({MediaType.TEXT_HTML})
    public InputStream get( @DefaultValue("") @QueryParam("auth") String auth ) {
        if(!UserDAO.checkAuth(auth))
            return Helper.getPage(servletContext, "accessDeniedPage.html");
        switch( UserDAO.getRole(auth) ) {
            case "manager":
                return Helper.getPage(servletContext, "seasonPage.html");
            default:
                return Helper.getPage(servletContext, "accessDeniedPage.html");
        }
    }
	
	@GET
	@Path("/all")
	public Response getSeasons( @DefaultValue("") @QueryParam("auth") String auth ) {
		if (!UserDAO.getRole(auth).equals("manager")) {
			return null;
		}
		String json = SeasonDAO.getAllSeasons(auth);
		return Response.ok(json).build();
	}
	
	@POST
	@Path("/create/{seasonName}-{startDate}-{endDate}-{roomTypes}-{prices}")
	public Response createTimePeriod(@DefaultValue("") @QueryParam("auth") String auth,
								     @PathParam("seasonName") String seasonName,
								     @PathParam("startDate") String startDate,
								     @PathParam("endDate") String endDate,
								     @PathParam("roomTypes") String roomTypes,
								     @PathParam("prices") String prices,
								     @FormParam("hotelID") int hotelID) {
		if (!UserDAO.checkRoleAndAuth(auth, "manager"))
			return null;
		//SeasonDAO.createTimePeriod();
		//SeasonDAO.createInitialPrice();
		//SeasonDAO.createOperatesDuring();
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/delete/{seasonName}-{start}-{end}")
	public Response deleteSeason(@DefaultValue("") @QueryParam("auth") String auth,
			@PathParam("seasonName") String seasonName,
			@PathParam("start") String start,
			@PathParam("end") String end) {
		if (!UserDAO.getRole(auth).equals("manager")) {
			return null;
		}
		SeasonDAO.deleteSeason(auth, seasonName, start, end);
		return Response.ok().build();
	}
}
