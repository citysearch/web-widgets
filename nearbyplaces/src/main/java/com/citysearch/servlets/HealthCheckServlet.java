package com.citysearch.servlets;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class HealthCheckServlet
 */
public class HealthCheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final Logger LOGGER = Logger.getLogger(getClass());

    private static String HEALTH_CHECK_RESPONSE_HEADER = "X-HealthCheck";

    private String singleServerHealthFile;
    private String fileParam = "file";

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Health Checking...");
        }

        singleServerHealthFile = request.getParameter(fileParam);
        LOGGER.info("File is : " + singleServerHealthFile);
        // Check for failover file
        if (!isFailoverSingleServer()) {
            failHealthCheck(response, "manual single webserver failover mode activated");
        } else {
            response.setHeader(HEALTH_CHECK_RESPONSE_HEADER, "healthcheck passed");
            response.setStatus(HttpServletResponse.SC_OK);
        }

    }

    private void failHealthCheck(HttpServletResponse response, String reason) {
        response.setHeader(HEALTH_CHECK_RESPONSE_HEADER, reason);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    }

    private boolean isFailoverSingleServer() {
        File failover = new File(singleServerHealthFile);
        if (failover.exists() && failover.isFile()) {
            LOGGER.info("Health Check passed");
            return true;
        }
        return false;
    }

}
