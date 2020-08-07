package controllers.reports;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.Employee;
import models.Report;
import models.validators.ReportValidator;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsGoodServlet
 */
@WebServlet("/reports/good")
public class ReportsGoodServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsGoodServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String _token = (String) request.getParameter("_token");
        //if (_token != null && _token.equals(request.getSession().getId())) {

        EntityManager em = DBUtil.createEntityManager();

        Report r = em.find(Report.class, Integer.parseInt(request.getParameter("id")));
        //
        List<Employee> employeeList = r.getEmployeeList();
        HttpSession session = ((HttpServletRequest) request).getSession();

        // セッションスコープに保存された従業員（ログインユーザ）情報を取得
        Employee e = (Employee) session.getAttribute("login_employee");
        employeeList.add(e);

        r.setEmployeeList(employeeList);
        r.setUpdated_at(new Timestamp(System.currentTimeMillis()));

        List<String> errors = ReportValidator.validate(r);
        if (errors.size() > 0) {
            em.close();

            request.setAttribute("_token", request.getSession().getId());
            request.setAttribute("report", r);
            request.setAttribute("errors", errors);

        } else {
            //System.out.println("aaa");
            em.getTransaction().begin();
            em.getTransaction().commit();
            em.close();
            request.getSession().setAttribute("flush", "更新が完了しました。");

        }

        //画像をクリックするとheart1からheart2に変わる。
        response.sendRedirect(request.getContextPath() + "/reports/index");
    }
}