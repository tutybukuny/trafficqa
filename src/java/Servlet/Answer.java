/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import core.FindingAnswer;
import core.dao.DAO;
import crfsuite.TrafficCrfTagger;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 *
 * @author tutyb
 */
@WebServlet(name = "Answer", urlPatterns = {"/Answer"})
public class Answer extends HttpServlet {

    String DATA_PATH = "";
    String modelPath = "";
    DAO dao = null;
    TrafficCrfTagger tagger = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        String action = request.getParameter("action");

        System.out.println(action);

        if (action.equals("getAnswer")) {
            getAnswers(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void getAnswers(HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        if (FindingAnswer.dao == null) {//create DAO
            String domain = request.getServerName();
            String username, password, dbName;
            if (domain.equals("localhost")) {
                username = "root";
                password = "";
                dbName = "qaservice";
            } else {
                domain = "127.12.52.2";
                username = "adminFf1Pbuj";
                password = "c5h8CW-Kb-HV";
                dbName = "QADatabase";
            }
            FindingAnswer.dao = new DAO(domain, username, password, dbName);
        }

        if (DATA_PATH.length() == 0) {
            String domain = request.getServerName();
            if (domain.equals("localhost")) {
                DATA_PATH = getServletContext().getRealPath("/") + "Data\\";
            } else {
                DATA_PATH = System.getenv("OPENSHIFT_DATA_DIR");
            }
            Const.Path.DATA_PATH = DATA_PATH;
//            modelPath = DATA_PATH + "model.crfsuite";
//            tagger = new TrafficCrfTagger(modelPath);
        }

        String question = URLDecoder.decode(request.getParameter("question"), "UTF-8").trim().replaceAll("\\s+", " ");
//        System.out.println(question);

//        ///tagging
//        List<Pair<String, String>> tags = tagger.tag(question);
//        HashMap<String, String> hash = new HashMap();
//        String content;
//        JSONArray jtags = new JSONArray();
//
//        for (int i = 0; i < tags.size(); i++) {
//            Pair<String, String> pair = tags.get(i);
//            String token = pair.getFirst();
//            String tag = pair.getSecond().replaceAll("B-", "").replaceAll("I-", "");
//            JSONObject jobj = new JSONObject();
//            jobj.put("token", token);
//            jobj.put("tag", pair.second);
//            jtags.put(jobj);
//
////            System.out.println(token + " " + tag);
//            if (!tag.equals("O")) {
//                content = ((hash.containsKey(tag)) ? hash.get(tag) + " " + token.trim() : token.trim());
//                hash.put(tag, content);
//            }
//        }
//
//        ///end tagging
//        standardizeHash(hash);

        String answer;

//        answer = dao.getClosestAnswer(hash);
        answer = FindingAnswer.getAnswer(question).get(0).getAnswer();

        answer = ((answer == null || answer.length() == 0) ? "No Answers!" : answer);

        JSONObject json = new JSONObject();
        json.put("answer", answer);
//        json.put("tags", jtags);

        System.out.println(answer);

        response.getWriter().write(json.toString());
    }
}
