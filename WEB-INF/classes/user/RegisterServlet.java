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
		String firstname = req.getParameter("firstname");
		String lastname = req.getParameter("lastname");
		String email = req.getParameter("email");
		String pwd = req.getParameter("password");
		String confirmPwd = req.getParameter("confirmPassword");

		if (checkNullEmpty(req,"firstname", firstname) || checkNullEmpty(req,"lastname", lastname) || checkNullEmpty(req,"email", email) || checkNullEmpty(req, "password", pwd)){
			req.getRequestDispatcher("/register.jsp").forward(req, resp);
		}else{
			if (!pwd.equals(confirmPwd)){
				req.setAttribute("errorMessage", "Le mot de passe et la confirmation ne correspondent pas");
				req.getRequestDispatcher("register.jsp").forward(req, resp);
			}else{
				long id = userDao.exists(email);
				if (id != -1){
					req.setAttribute("emailError", "Un utilisateur avec cet email existe déjà");
					req.getRequestDispatcher("/register.jsp").forward(req, resp);
				}else{
					// Création utilisateur
					userDao.add(new User(firstname, lastname, email, pwd), pwd);
					resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
					resp.sendRedirect("/auth.jsp");
				}
			}
		}
	}

	private boolean checkNullEmpty(HttpServletRequest req, String element, String value) {
		if (value == null || value.isEmpty()){
			req.setAttribute("errorMessage", "le champ : " + element + " n'est pas valide.");
			return true;
		}
		return false;
	}
}
