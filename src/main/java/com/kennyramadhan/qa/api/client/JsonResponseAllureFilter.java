package com.kennyramadhan.qa.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * RestAssured filter that attaches the response body to Allure as a
 * pretty-printed JSON attachment when the body content is JSON, regardless of
 * the server-declared Content-Type header.
 *
 * <p>
 * Rationale: automationexercise.com returns JSON response bodies with
 * Content-Type: text/html; charset=ISO-8859-1. The default AllureRestAssured
 * filter renders the body verbatim, producing &lt;html&gt;&lt;body&gt;-wrapped
 * output that is not readable as JSON in the Allure report.
 *
 * <p>
 * This filter complements (does not replace) AllureRestAssured: the underlying
 * request/response trace from AllureRestAssured remains for curl-equivalent
 * debugging; this filter adds a separate JSON attachment that renders properly
 * with Allure's syntax highlighter.
 *
 * <p>
 * Detection is lexical: bodies starting with '{' or '[' (after trim) are
 * treated as JSON candidates. Bodies that look like JSON but fail to parse are
 * silently skipped — the underlying test action must never fail because of an
 * attachment helper.
 */
public class JsonResponseAllureFilter implements Filter {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec,
			FilterContext ctx) {
		Response response = ctx.next(requestSpec, responseSpec);
		String body = response.getBody().asString();
		if (looksLikeJson(body)) {
			try {
				Object parsed = MAPPER.readValue(body, Object.class);
				String pretty = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(parsed);
				Allure.addAttachment("Response Body (JSON)", "application/json", pretty, ".json");
			} catch (Exception e) {
				// Body looked like JSON but failed to parse — skip the attachment silently
				// rather than fail the test action.
			}
		}
		return response;
	}

	private static boolean looksLikeJson(String body) {
		if (body == null || body.isBlank()) {
			return false;
		}
		String trimmed = body.trim();
		return trimmed.startsWith("{") || trimmed.startsWith("[");
	}
}
