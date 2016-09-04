package ua.nptest.controller;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.nptest.model.Country;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

@Controller
public class MainController {

    private static String path = "http://185.89.242.156:8280/services/reference?username=topway&name=country&lang=uk";

    private static String json = jsonFromResponse(path);

    private final static Logger LOG = Logger.getLogger(MainController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String first() {
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String second(@ModelAttribute(value = "id") String id, Model model) {

        LOG.info("In second method");
        if (id.equals("")) {
            model.addAttribute("error", "Put id first");
            return "index";
        }
        try {
            LOG.info("ID:" + id);
            String country = getCountryById(Integer.parseInt(id.trim()));
            model.addAttribute("name", country);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "index";
    }

    private static String getCountryById(int id) throws Exception {
        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject(json);
        JSONObject responseLevel = (JSONObject) jsonObject.get("response");
        JSONObject dataLevel = (JSONObject) responseLevel.get("data");
        JSONArray countries = (JSONArray) dataLevel.get("Country");

        String finalJson = countries.toString();

        Country[] countries1 = gson.fromJson(finalJson, Country[].class);

        String countryName;
        for (int i = 0; i < countries1.length; i++) {
            if (countries1[i].getId() == id) {
                countryName = countries1[i].getName();
                return countryName;
            }
            if (i == countries1.length - 1 && countries1[i].getId() != id) {
                throw new Exception("Country with ID: " + id + " not found");
            }
        }
        return null;
    }

    private static String jsonFromResponse(String url) {
        URL urlConn;
        try {
            urlConn = new URL(url);
            URLConnection conn = urlConn.openConnection();
            Reader is = new InputStreamReader(conn.getInputStream(), "UTF-8");
            int data = is.read();
            char symb;
            String json = "";
            while (data != -1) {
                symb = (char) data;
                json += symb;
                data = is.read();
            }
            is.close();
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
