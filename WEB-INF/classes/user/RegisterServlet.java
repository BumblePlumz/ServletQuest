package user;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns="/register")
public class RegisterServlet extends HttpServlet {
	
	private UserDao userDao;
	
	@Override
	public void init() {
		try {
			this.userDao = new UserDaoSqlite("C:\\xampp\\tomcat\\webapps\\ServletQuest\\WEB-INF\\users.db");
		} catch (SQLException e) {
			System.out.println("Erreur dans l'init");
		}
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Récupération des inputs du formulaire
		String firstname = req.getParameter("firstname").toLowerCase().trim();
		String lastname = req.getParameter("lastname").toLowerCase().trim();
		String email = req.getParameter("email").toLowerCase().trim();
		String pwd = req.getParameter("pwd").trim();
		String confirmPwd = req.getParameter("pwdConfirm").trim();

		if ((pwd.isEmpty() || confirmPwd.isEmpty()) || (!pwd.isEmpty() && !confirmPwd.isEmpty() && !pwd.equals(confirmPwd))){
			req.setAttribute("errorMsg", "Une erreur est survenue dans la création d'un utilisateur");
			resp.sendRedirect("/register.jsp");
		}

		// Création utilisateur
		System.out.println(firstname);
		System.out.println(lastname);
		System.out.println(email);
		System.out.println(pwd);
		System.out.println(confirmPwd);
		User u = new User(firstname, lastname, email, pwd);
		userDao.add(u, u.getPassword());
		long id = userDao.exists(email);
		if (id == -1){
			req.setAttribute("errorMsg", "Une erreur est survenue dans la création d'un utilisateur");
			resp.sendRedirect("/register.jsp");
		}
		// Rediretion
		RequestDispatcher rq = req.getRequestDispatcher("/auth.jsp");
		rq.forward(req, resp);
	}

}
