package auth;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpSession;
import user.User;
import user.UserDao;
import user.UserDaoSqlite;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
	private String error = "Vous avez entrer un mauvais identifiant/mot de passe";

	// @Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		String login = req.getParameter("email").toLowerCase().trim();
		String password = req.getParameter("password");

		if ( login == null || password == null ) {
			req.setAttribute("error", error);
			resp.sendRedirect("auth.jsp");
		}

		try {
			UserDao userDao = new UserDaoSqlite("C:\\xampp\\tomcat\\webapps\\ServletQuest\\WEB-INF\\users.db");
			long id = userDao.checkPassword(login, password);
			if (id != -1){
				User u = userDao.find(id);
				HttpSession session = req.getSession();
				session.setAttribute("email", login);
				session.setAttribute("firstname", u.getFirstname());
				session.setAttribute("name", u.getLastname());

				// Redirection interne au serveur
				req.getRequestDispatcher("welcome.jsp").forward(req, resp);
			}else{
				req.setAttribute("error", error);
				// Redirection par le navigateur et l'url
				resp.sendRedirect("auth.jsp");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		// check for "logout" parameter
		String logout = (String) req.getParameter( "logout" );
		System.out.print("logout:"+logout);
		if(logout!=null){
			//suppression de la session
			HttpSession session = req.getSession();
			session.removeAttribute("login");
			session.invalidate();

			//redirection
			RequestDispatcher rd =
					req.getRequestDispatcher( "auth.jsp" );

			rd.forward(req, resp);
		}

		//   if not : Error 500
		else {
			System.out.println("error500");
			resp.sendError(SC_INTERNAL_SERVER_ERROR);
		}
	}

}