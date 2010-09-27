package com.citysearch.webwidget.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.PropertiesLoader;

public class CitySearchBootstrapServlet extends HttpServlet {
	private final Logger LOGGER = Logger.getLogger(getClass());

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		LOGGER.info("Initializing application.........");
		String propertyFileName = System.getProperty("CONTENTADS_PROPERTIES");
		if (propertyFileName != null) {
			LOGGER.info("Loading " + propertyFileName);
			try {
				PropertiesLoader.loadAPIProperties(propertyFileName);
			} catch (CitysearchException exp) {
				LOGGER.error("*** System Property CONTENTADS_PROPERTIES = "
						+ propertyFileName);
				LOGGER.error(exp.getMessage());
			}
			LOGGER.info("Application initialized successfully.");
		} else {
			LOGGER.error("*** Could not find Properties file to load ***");
			LOGGER.error("*** System Property CONTENTADS_PROPERTIES is "
					+ propertyFileName);
			LOGGER.info("Application failed to initialize.");
		}

		LOGGER.info("Initializing application.........Done");
	}

}
