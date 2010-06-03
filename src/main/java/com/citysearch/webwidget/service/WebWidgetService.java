package com.citysearch.webwidget.service;

import javax.jws.WebService;

import org.codehaus.jra.Get;
import org.codehaus.jra.HttpResource;

@WebService
public interface WebWidgetService {
	@Get
	@HttpResource(location = "/reviewSvc")
	String getString();

}
