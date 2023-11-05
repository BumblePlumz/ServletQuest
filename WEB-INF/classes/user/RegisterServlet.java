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
			this.userDao = new UserDaoSqlite("../../users.db");
		} catch (SQLException e) {
			System.out.println("Erreur dans l'init");
		}
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Récupération des inputs du formulaire
		String firstname = req.getParameter("firstname");
		String lastname = req.getParameter("lastname");
		String email = req.getParameter("email");
		String pwd = req.getParameter("pwd");
		String confirmPwd = req.getParameter("pwdConfirm");
		System.out.println(firstname);
		System.out.println(lastname);
		System.out.println(email);
		System.out.println(pwd);
		System.out.println(confirmPwd);

		if ((pwd.isEmpty() || confirmPwd.isEmpty()) || (!pwd.isEmpty() && !confirmPwd.isEmpty() && !pwd.equals(confirmPwd))){
			req.setAttribute("errorMsg", "Une erreur est survenue dans la création d'un utilisateur");
			resp.sendRedirect("/register.jsp");
		}

		// Création utilisateur
		userDao.add(new User(firstname, lastname, email), pwd);
		long id = userDao.exists(email);
		if (id == -1){
			req.setAttribute("errorMsg", "Une erreur est survenue dans la création d'un utilisateur");
			resp.sendRedirect("/register.jsp");
		}
		// Rediretion
		RequestDispatcher rq = req.getRequestDispatcher("/register.jsp");
		rq.forward(req, resp);
	}

}
