/*
 * Copyright 2016-2017 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.reactor.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.StreamingOutput;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.holonplatform.core.internal.utils.ConversionUtils;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.http.exceptions.UnsuccessfulResponseException;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.reactor.http.ReactiveResponseEntity;
import com.holonplatform.reactor.http.ReactiveRestClient;
import com.holonplatform.reactor.spring.SpringReactiveRestClient;
import com.holonplatform.spring.EnableBeanContext;
import com.holonplatform.test.JerseyTest5;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestReactiveRestClient.Config.class)
public class TestReactiveRestClient extends JerseyTest5 {

	@Configuration
	@EnableBeanContext
	static class Config {

		@Bean
		public WebClient webClient() {
			return WebClient.create();
		}

	}

	public TestReactiveRestClient() {
		super();
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	@Path("test")
	public static class TestResource {

		@GET
		@Path("data/{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public TestData getData(@PathParam("id") int id) {
			return new TestData(id, "value" + id);
		}

		@GET
		@Path("data2/{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getData2(@PathParam("id") int id) {
			if (id < 0) {
				return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
						.entity(new ApiError("ERR000", "Invalid data")).build();
			}
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(new TestData(id, "value" + id)).build();
		}

		@GET
		@Path("data")
		@Produces(MediaType.APPLICATION_JSON)
		public List<TestData> getBoxes() {
			List<TestData> boxes = new LinkedList<>();
			boxes.add(new TestData(1, "One"));
			boxes.add(new TestData(2, "Two"));
			return boxes;
		}

		@GET
		@Path("stream")
		@Produces(MediaType.APPLICATION_OCTET_STREAM)
		public StreamingOutput getStream() {
			return new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					output.write(new byte[] { 1, 2, 3 });
				}
			};
		}

		@POST
		@Path("formParams")
		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		public Response formParams(@FormParam("one") String one, @FormParam("two") Integer two) {
			assertNotNull(one);
			assertNotNull(two);
			if (!two.toString().equals(one)) {
				return Response.status(Status.BAD_REQUEST).build();
			}
			return Response.ok().build();
		}

		@PUT
		@Path("data/save")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response saveData(TestData data) {
			if (data == null || data.getCode() < 0) {
				return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
						.entity(new ApiError("ERR000", "Invalid data")).build();
			}
			return Response.accepted().build();
		}

		@GET
		@Path("status/400")
		public Response get400() {
			return Response.status(Status.BAD_REQUEST).build();
		}

		@GET
		@Path("status/401")
		public Response get401() {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		@GET
		@Path("status/403")
		public Response get403() {
			return Response.status(Status.FORBIDDEN).build();
		}

		@GET
		@Path("status/405")
		public Response get405() {
			return Response.status(Status.METHOD_NOT_ALLOWED).build();
		}

		@GET
		@Path("status/406")
		public Response get406() {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}

		@GET
		@Path("status/415")
		public Response get415() {
			return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).build();
		}

		@GET
		@Path("status/500")
		public Response get500() {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		@GET
		@Path("status/503")
		public Response get503() {
			return Response.status(Status.SERVICE_UNAVAILABLE).build();
		}

	}

	public static class TestData {

		private int code;
		private String value;

		public TestData() {
			super();
		}

		public TestData(int code, String value) {
			super();
			this.code = code;
			this.value = value;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + code;
			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestData other = (TestData) obj;
			if (code != other.code)
				return false;
			return true;
		}

	}

	public static class ApiError {

		private String code;
		private String message;

		public ApiError() {
			super();
		}

		public ApiError(String code, String message) {
			super();
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	@Autowired
	private WebClient webClient;

	@Override
	protected Application configure() {
		return new ResourceConfig(TestResource.class);
	}

	@Test
	public void testFactory() {
		ReactiveRestClient client = ReactiveRestClient.create();
		assertNotNull(client);

		client = ReactiveRestClient.create(SpringReactiveRestClient.class.getName());
		assertNotNull(client);
	}

	@Test
	public void testClient() {

		final ReactiveRestClient client = SpringReactiveRestClient.create(webClient).defaultTarget(getBaseUri());

		Mono<TestData> td = client.request().path("test").path("data/{id}").resolve("id", 1)
				.getForEntity(TestData.class);
		StepVerifier.create(td).consumeNextWith(d -> assertEquals(1, d.getCode())).expectComplete().verify();

		Flux<TestData> tds = client.request().path("test").path("data").getAsList(TestData.class);
		StepVerifier.create(tds).consumeNextWith(d -> assertEquals(1, d.getCode()))
				.consumeNextWith(d -> assertEquals(2, d.getCode())).expectComplete().verify();

		Mono<ReactiveResponseEntity<TestData>> rspe = client.request().path("test").path("data/{id}").resolve("id", 1)
				.get(TestData.class);
		StepVerifier.create(rspe).consumeNextWith(r -> {
			assertNotNull(r.getHeaders());
			assertEquals(TestData.class, r.getPayloadType());
		}).expectComplete().verify();

		Mono<String> asString = client.request().path("test").path("data/{id}").resolve("id", 1).get(TestData.class)
				.flatMap(r -> r.asMono(String.class));
		StepVerifier.create(asString).consumeNextWith(s -> assertNotNull(s)).expectComplete().verify();

		Mono<ReactiveResponseEntity<Void>> rsp = client.request().path("test").path("data/save")
				.put(RequestEntity.json(new TestData(7, "testPost")));
		StepVerifier.create(rsp).consumeNextWith(r -> assertEquals(HttpStatus.ACCEPTED, r.getStatus())).expectComplete()
				.verify();

		rsp = client.request().path("test").path("formParams")
				.post(RequestEntity.form(RequestEntity.formBuilder().set("one", "1").set("two", "1").build()));
		StepVerifier.create(rsp).consumeNextWith(r -> assertEquals(HttpStatus.OK, r.getStatus())).expectComplete()
				.verify();

	}

	@Test
	public void testBean() {

		final ReactiveRestClient client = ReactiveRestClient.create().defaultTarget(getBaseUri());

		Mono<TestData> td = client.request().path("test").path("data/{id}").resolve("id", 1)
				.getForEntity(TestData.class);
		StepVerifier.create(td).consumeNextWith(d -> assertEquals(1, d.getCode())).expectComplete().verify();

	}

	@Test
	public void testReads() {
		final ReactiveRestClient client = SpringReactiveRestClient.create(webClient).defaultTarget(getBaseUri());

		Mono<TestData> res = client.request().path("test").path("data2/{id}").resolve("id", 1).get(TestData.class)
				.flatMap(r -> r.asMono(TestData.class));
		StepVerifier.create(res).consumeNextWith(d -> assertEquals(1, d.getCode())).expectComplete().verify();

	}

	@Test
	public void testStream() {

		final ReactiveRestClient client = SpringReactiveRestClient.create(webClient).defaultTarget(getBaseUri());

		Mono<InputStream> s = client.request().path("test").path("stream").getForStream();
		StepVerifier.create(s).consumeNextWith(r -> {
			try {
				byte[] bytes = ConversionUtils.convertInputStreamToBytes(r);
				assertTrue(Arrays.equals(new byte[] { 1, 2, 3 }, bytes));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).expectComplete().verify();

	}

	@Test
	public void testErrors() {
		final ReactiveRestClient client = SpringReactiveRestClient.create(webClient).defaultTarget(getBaseUri());

		Mono<ReactiveResponseEntity<TestData>> r2 = client.request().path("test").path("data2/{id}").resolve("id", -1)
				.get(TestData.class);
		StepVerifier.create(r2).consumeNextWith(r -> assertEquals(HttpStatus.BAD_REQUEST, r.getStatus()))
				.expectComplete().verify();

		Mono<ApiError> error = client.request().path("test").path("data2/{id}").resolve("id", -1).get(TestData.class)
				.flatMap(r -> r.asMono(ApiError.class));
		StepVerifier.create(error).consumeNextWith(e -> assertEquals("ERR000", e.getCode())).expectComplete().verify();

		Mono<TestData> fail = client.request().path("test").path("data2/{id}").resolve("id", -1)
				.getForEntity(TestData.class);
		StepVerifier.create(fail).expectError(UnsuccessfulResponseException.class).verify();

		fail = client.request().path("test").path("data2/{id}").resolve("id", -1).getForEntity(TestData.class);
		StepVerifier.create(fail).expectErrorSatisfies(e -> {
			assertTrue(e instanceof UnsuccessfulResponseException);
			UnsuccessfulResponseException ure = (UnsuccessfulResponseException) e;
			assertEquals(HttpStatus.BAD_REQUEST, ure.getStatus().orElse(null));
			assertNotNull(ure.getResponse());
		}).verify();

	}

	@Test
	public void testStatus() {

		final ReactiveRestClient client = SpringReactiveRestClient.create(webClient).defaultTarget(getBaseUri());

		Mono<ReactiveResponseEntity<Void>> rsp = client.request().path("test").path("status").path("400")
				.get(Void.class);
		StepVerifier.create(rsp).consumeNextWith(r -> assertEquals(HttpStatus.BAD_REQUEST, r.getStatus()))
				.expectComplete().verify();

		rsp = client.request().path("test").path("status").path("401").get(Void.class);
		StepVerifier.create(rsp).consumeNextWith(r -> assertEquals(HttpStatus.UNAUTHORIZED, r.getStatus()))
				.expectComplete().verify();

		rsp = client.request().path("test").path("status").path("403").get(Void.class);
		StepVerifier.create(rsp).consumeNextWith(r -> assertEquals(HttpStatus.FORBIDDEN, r.getStatus()))
				.expectComplete().verify();

		rsp = client.request().path("test").path("status").path("405").get(Void.class);
		StepVerifier.create(rsp).consumeNextWith(r -> assertEquals(HttpStatus.METHOD_NOT_ALLOWED, r.getStatus()))
				.expectComplete().verify();

		rsp = client.request().path("test").path("status").path("406").get(Void.class);
		StepVerifier.create(rsp).consumeNextWith(r -> assertEquals(HttpStatus.NOT_ACCEPTABLE, r.getStatus()))
				.expectComplete().verify();

		rsp = client.request().path("test").path("status").path("415").get(Void.class);
		StepVerifier.create(rsp).consumeNextWith(r -> assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, r.getStatus()))
				.expectComplete().verify();

		rsp = client.request().path("test").path("status").path("500").get(Void.class);
		StepVerifier.create(rsp).consumeNextWith(r -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, r.getStatus()))
				.expectComplete().verify();

		rsp = client.request().path("test").path("status").path("503").get(Void.class);
		StepVerifier.create(rsp).consumeNextWith(r -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, r.getStatus()))
				.expectComplete().verify();

	}

}
