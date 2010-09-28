package com.citysearch.webwidget.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.regionName;
import com.maxmind.geoip.timeZone;

/**
 * Servlet implementation class SkyNetSearchServlet
 */
public class SkyNetSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private HashMap cacheMap = new HashMap();
    private ArrayList cacheKeyList = new ArrayList();
    ResourceBundle properties = ResourceBundle.getBundle("PropertiesBundle");
    String cacheLimit = properties.getString("cacheLimit");
    String dbfile = properties.getString("DBFilePath");;
    LookupService cl = null;

    /**
     * Default constructor.
     */
    public SkyNetSearchServlet() {
        // TODO Auto-generated constructor stub

    }

    public void init() {
        try {
            // You should only call LookupService once, especially if you use
            // GEOIP_MEMORY_CACHE mode, since the LookupService constructor takes up
            // resources to load the GeoIP.dat file into memory

            cl = new LookupService(dbfile, LookupService.GEOIP_MEMORY_CACHE);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub

        String strCity = "";
        String strCountry = "";
        PrintWriter out = response.getWriter();
        response.setContentType("text/xml");
        String urlFromRquestParam = (String) request.getParameter(("url"));
        String apiUrl = properties.getString("API_URL");
        String urlString = apiUrl + urlFromRquestParam;
        URL url = new URL(urlString);
        
        System.out.println(urlString);
        System.out.println("ip:" + request.getRemoteAddr() + " host:" + request.getRemoteHost());

        String ipAddress = request.getRemoteAddr();
        //ipAddress ="74.208.64.80";

        // System.out.println(cl.getCountry(request.getRemoteHost()).getName());
        String cacheLimit = properties.getString("cacheLimit");
        System.out.println("cacheLimit is:" + cacheLimit);

        if (!cacheMap.containsKey(urlString)) {

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            System.out.println(conn.getResponseCode());
            try {
                if (conn.getResponseCode() == 200) {
                    if (cacheKeyList.size() == Integer.parseInt(cacheLimit)) {
                        String removedKey = (String) cacheKeyList.remove(0);
                        cacheMap.remove(removedKey);
                        System.out.println("cacheLimit size reached: " + cacheLimit
                                + " so removing: " + removedKey);
                    }
                    InputStream inputStream = conn.getInputStream();
                    System.out.println(inputStream);
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    String lineOutput = "";
                    int i = 0;
                    while ((line = in.readLine()) != null) {
                        lineOutput = lineOutput + line;
                    }
                    cacheMap.put(urlString, lineOutput);
                    cacheKeyList.add(urlString);
                    lineOutput = addCityStateInformation(new StringBuffer(lineOutput), ipAddress);
                    out.println(lineOutput);
                } else {
                    /*
                     * this code is for just testing hard coded stuff when server is down String
                     * output =
                     * "<ns1:GetResultsResponse xmlns:ns1=\"http://skynet.services.netseer.com/\"><url>http://www.facebook.com</url><topCategories><category><categoryID>1555</categoryID><score>0.8877098560333252</score><name>Internet&amp; Software/Social Networking</name></category></topCategories><keywords><keyword><relatedKeyword>ConnectingPeople</relatedKeyword><id>-6977679248994707072</id></keyword></keywords></ns1:GetResultsResponse>"
                     * ; output = addCountryInformation(new StringBuffer(output),ipAddress);
                     * out.println(output);
                     */

                    out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> <Status>No Response or Invalid URL</Status>");
                }
            } catch (IOException ex) {
                out.println(ex.getLocalizedMessage());
            }
        } else {
            System.out.println("Getting from cache........");

            String output = (String) cacheMap.get(urlString);
            output = addCityStateInformation(new StringBuffer(output), ipAddress);
            out.println(output);
        }

        System.out.println("cacheMap is:" + cacheMap.size());
    }

    private String addCityStateInformation(StringBuffer stringBuffer, String ipAddress) {
        // TODO Auto-generated method stub
        try {

            Location l1 = cl.getLocation(ipAddress);
            String toInsert = "<city>" + l1.city + "</city><state>"
                    + regionName.regionNameByCode(l1.countryCode, l1.region) + "</state><country>"
                    + l1.countryName + "</country>";
            System.out.println("countryCode: " + l1.countryCode + "\n countryName: "
                    + l1.countryName + "\n region: " + l1.region + "\n regionName: "
                    + regionName.regionNameByCode(l1.countryCode, l1.region) + "\n city: "
                    + l1.city + "\n postalCode: " + l1.postalCode + "\n latitude: " + l1.latitude
                    + "\n longitude: " + l1.longitude + "\n metro code: " + l1.metro_code
                    + "\n area code: " + l1.area_code);

            // String toInsert = "<country>"+cl.getCountry(ipAddress).getName()+"</country>";

            System.out.println("toInsert-->" + toInsert);
            int urlIndex = stringBuffer.indexOf("<url>");
            if (urlIndex == -1) {
                urlIndex = stringBuffer.indexOf("<URL>");
            }
            if (urlIndex != -1)
                stringBuffer.insert(urlIndex, toInsert);

            System.out.println("urlinddex-->" + urlIndex + "New Output:" + stringBuffer.toString());
        } catch (Exception e) {
            System.out.println("IO Exception" + e);
        }

        return stringBuffer.toString();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    private String addCountryInformation(StringBuffer stringBuffer, String ipAddress) {
        // TODO Auto-generated method stub
        try {

            String toInsert = "<country>" + cl.getCountry(ipAddress).getName() + "</country>";
            System.out.println("toInsert-->" + toInsert);
            int urlIndex = stringBuffer.indexOf("<url>");
            if (urlIndex == -1) {
                urlIndex = stringBuffer.indexOf("<URL>");
            }
            if (urlIndex != -1)
                stringBuffer.insert(urlIndex, toInsert);

            System.out.println("urlinddex-->" + urlIndex + "New Output:" + stringBuffer.toString());
        } catch (Exception e) {
            System.out.println("IO Exception" + e);
        }

        return stringBuffer.toString();
    }

}
