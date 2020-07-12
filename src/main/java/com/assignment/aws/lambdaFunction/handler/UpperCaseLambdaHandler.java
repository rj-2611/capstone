package com.assignment.aws.lambdaFunction.handler;

import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

@Component
public class UpperCaseLambdaHandler implements Function<APIGatewayProxyRequestEvent,String> {
	
	private Logger logger = LoggerFactory.getLogger(UpperCaseLambdaHandler.class);
	
	@Override
	public String apply(final APIGatewayProxyRequestEvent event) {
		
		String[] rating = event.getBody().split("=");
		logger.info(rating[0]);
		logger.info(rating[1]);
		logger.info("rating = " + event.getBody());
		
		try {
			final AmazonDynamoDBClient client = new AmazonDynamoDBClient(new EnvironmentVariableCredentialsProvider());
	        client.withRegion(Regions.AP_SOUTH_1); // specify the region you created the table in.
	        DynamoDB dynamoDB = new DynamoDB(client);
	        Table table = dynamoDB.getTable("feedback");
	        
	        logger.info("Connection established.");
	        final Item item = new Item()
	                .withPrimaryKey("id", UUID.randomUUID().toString()) // Every item gets a unique id
	                .withString("rating", rating[1]);
	                
	        table.putItem(item);
	        logger.info("Value " + rating[1] + " set into rating field.");
	        String serviceRating = "";
	        if(rating[1].equals("1")) {
	        	serviceRating = "Poor";
	        }
	        if(rating[1].equals("2")) {
	        	serviceRating = "Average";
	        }
	        else {
	        	serviceRating = "Excellent";
	        }
	        String response = "<!DOCTYPE html><div style=\"text-align:center;\"><H3>Thanks for rating our service as: "+ serviceRating + "</H3><br><p>Your feedback is important to us</p><br>Go back to <a href=\"#\">Home</a></div>";
	        
			logger.info("Success");
			return response;
		} catch(Exception e) {
			return "<!DOCTYPE html><h3>Something was not right</h3>";
		}
	}
}
