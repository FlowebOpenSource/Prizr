package fi.floweb.prizr.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import fi.floweb.prizr.beans.MultiplierBase;
import fi.floweb.prizr.beans.PricingRequest;
import fi.floweb.prizr.beans.PricingResponse;
import fi.floweb.prizr.facts.FactStorage;
import fi.floweb.prizr.facts.FactStorageMongoDBImpl;

@Path("/basic")
public class Pricing {

	@Context
	private ServletContext application;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String sayPlainTextHello() {
    return "OK";
  }
 
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public PricingResponse getPricing(PricingRequest req) {
	  KieSession kSession = (KieSession) application.getAttribute("ksession");
	  if(kSession == null) {
		  return null;
	  } else {
		  kSession.insert(req);
		  PricingResponse res = new PricingResponse();
		  FactHandle resHandle = kSession.insert(res);
	      kSession.fireAllRules();
	      PricingResponse response = (PricingResponse) kSession.getObject(resHandle);
	      System.out.println("Responded with PricingResponse:");
	      System.out.println(response);
	      return response;
	  }
  }
  
  @Path("/rules")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public MultiplierBase[] getRules() {
	  System.out.println("Got request to return rules...");
	  FactStorage storage = new FactStorageMongoDBImpl();
	  ArrayList<MultiplierBase> rules = storage.getFacts();
	  MultiplierBase[] res = new MultiplierBase[rules.size()]; 
	  rules.toArray(res);
	  return res;
  }
  
  @Path("/rules")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public String setRule(MultiplierBase rule) {
	  System.out.println("Got request to set rule: "+rule.getMultiplierBaseName());
	  FactStorage storage = new FactStorageMongoDBImpl();
	  storage.storeFact(rule);
	  return "OK";
  }

} 