package web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class rederect
 */
public class Rederect extends HttpServlet {
	public Rederect() {

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		request.getSession().setAttribute("loging", "true");
		System.out.print("logged "
				+ request.getSession().getAttribute("loging"));
		RequestDispatcher rd = request.getRequestDispatcher("index.jsp");

		rd.forward(request, response);// method may be include or forward

	}
}
