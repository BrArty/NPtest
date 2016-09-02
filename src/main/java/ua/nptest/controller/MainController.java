package ua.nptest.controller;

import com.google.gson.Gson;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Controller
public class MainController {

    private String path = "http://185.89.242.156:8280/services/reference?username=topway&name=country&lang=uk";

    private String json = jsonFromResponse(path);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String first(Model model) {
        model.addAttribute("id", "Id and country expect here");
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String second(@ModelAttribute(value = "id") String id, Model model) {

        System.out.println("In second method");
        if (id.equals("")) {
            model.addAttribute("error", "Put id first");
            return "index";
        }
        try {
            System.out.println("In try block");
            System.out.println(id);
            String country = getCountryById(Integer.parseInt(id.trim()), path);
            System.out.println(country + "country");
            model.addAttribute("name", country);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "index";
    }

    private static String getCountryById(int id, String path) throws Exception {
        Gson gson = new Gson();
        String json = null;
        json = jsonFromResponse(path);
        JSONObject jsonObject = new JSONObject(json);
        JSONObject responseLevel = (JSONObject) jsonObject.get("response");
        JSONObject dataLevel = (JSONObject) responseLevel.get("data");
        JSONArray countries = (JSONArray) dataLevel.get("Country");

        String finalJson = countries.toString();

        Country[] countries1 = gson.fromJson(finalJson, Country[].class);

        String countryName = null;

        for (int i = 0; i < countries1.length; i++) {
            if (countries1[i].getId() == id) {
                countryName = countries1[i].getName();
                return countryName;
            }
            if (i == countries1.length - 1 && countries1[i].getId() != id) {
                throw new Exception("Country with this id not found");
            }
        }
        return null;
    }

    private static String jsonFromResponse(String url) {
        URL urlConn = null;
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
            return json;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
