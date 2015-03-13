package web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class test
 */
public class Authenticate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public Authenticate() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		System.out.println("header " + request.getParameter("header"));
		System.out.println("start " + request.getParameter("start"));
		if (request.getSession().getAttribute("loging") == null
				&& request.getParameter("header") == null) {
			response.sendRedirect("loging.jsp");

		} else {
			response.sendRedirect("viewFlight.jsp");
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("header " + request.getParameter("header"));
		//check for header is null and redirect to the login page
		if (request.getSession().getAttribute("loging") == null
				&& request.getParameter("header") == null) {

			response.sendRedirect("loging.jsp");

		} else {
			//else redirect to the viewFlight page with start and end destination with the url
			String destination3 = (request.getParameter("destination3"));
			String destination1 = (request.getParameter("destination1"));
			

			request.setAttribute("startDestination", destination3);
			request.setAttribute("endDestination", destination1);

			
			response.sendRedirect("viewFlight.jsp?startDestination="
					+ destination3 + "&endDestination=" + destination1);
		}

	}
}
