package vincentlow.twittur.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseIntegrationTest {

  protected final String ACCOUNT_ENTITY_DIR = "account";

  protected final String ACCOUNT_RELATIONSHIP_ENTITY_DIR = "account-relationship";

  protected final int PAGE_NUMBER = 0;

  protected final int PAGE_SIZE = 10;

  protected static final List<String> ignoredFields =
      Arrays.asList("id", "createdBy", "createdDate", "updatedBy", "updatedDate");

  @Autowired
  protected MockMvc mockMvc;

  protected ObjectMapper objectMapper;

  protected Map<String, String> params;

  protected MultiValueMap<String, String> paginationParams;

  @BeforeAll
  protected void setUpBeforeAll() {

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    params = new HashMap<>();
    params.put("pageNumber", String.valueOf(PAGE_NUMBER));
    params.put("pageSize", String.valueOf(PAGE_SIZE));
  }

  protected <T> T getRequestFromPath(String controllerDir, String jsonFileName, TypeReference<T> typeRef) {

    String methodName = Thread.currentThread()
        .getStackTrace()[2].getMethodName();
    String requestJsonPath =
        String.format("json/%s/%s/request/%s.json", controllerDir, methodName, jsonFileName);

    InputStream requestJsonInputStream = this.getClass()
        .getClassLoader()
        .getResourceAsStream(requestJsonPath);

    try {
      String requestJson = IOUtils.toString(requestJsonInputStream);
      return objectMapper.readValue(requestJson, typeRef);
    } catch (Exception e) {
      log.error("#getRequestFromPath ERROR! with controllerDir: {}, jsonFileName: {} and error: {}", controllerDir,
          jsonFileName, e.getMessage(), e);
      return null;
    }
  }

  protected <T> T getExpectationFromPath(String controllerDir, TypeReference<T> typeRef) {

    String methodName = Thread.currentThread()
        .getStackTrace()[2].getMethodName();
    String expectationJsonPath =
        String.format("json/%s/%s/expectation/api-response.json", controllerDir, methodName);

    InputStream expectationJsonInputStream = this.getClass()
        .getClassLoader()
        .getResourceAsStream(expectationJsonPath);

    try {
      String expectationJson = IOUtils.toString(expectationJsonInputStream);
      return objectMapper.readValue(expectationJson, typeRef);
    } catch (Exception e) {
      log.error("#getExpectationFromPath ERROR! with controllerDir: {}, and error: {}", controllerDir, e.getMessage(),
          e);
      return null;
    }
  }

  protected <T> T getEntityFromPath(String entityDir, String jsonFileName, TypeReference<T> typeRef) {

    String methodName = Thread.currentThread()
        .getStackTrace()[2].getMethodName();
    String entityJsonPath =
        String.format("json/entity/%s/%s.json", entityDir, jsonFileName, methodName);

    InputStream expectationJsonInputStream = this.getClass()
        .getClassLoader()
        .getResourceAsStream(entityJsonPath);

    try {
      String entityJson = IOUtils.toString(expectationJsonInputStream);
      return objectMapper.readValue(entityJson, typeRef);
    } catch (Exception e) {
      log.error("#getEntityFromPath ERROR! with entityDir:{}, jsonFileName: {}, and error: {}", entityDir, jsonFileName,
          e.getMessage(), e);
      return null;
    }
  }

  protected String objectToContentString(Object object) {

    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("#contentObjectToString ERROR! with object: {}, and error: {}", object, e.getMessage(), e);
      return null;
    }
  }

  protected <T> T getMvcResponse(MvcResult mvcResult, TypeReference<T> typeRef) {

    try {
      return objectMapper.readValue(mvcResult.getResponse()
          .getContentAsString(), typeRef);
    } catch (JsonProcessingException e) {
      log.error("#contentObjectToString ERROR! with mvcResult: {}, and error: {}", mvcResult, e.getMessage(), e);
      return null;
    } catch (UnsupportedEncodingException e) {
      log.error("#contentObjectToString ERROR! with mvcResult: {}, and error: {}", mvcResult, e.getMessage(), e);
      return null;
    }
  }

  protected void baseSuccessApiSingleResponseAssertion(ApiSingleResponse response) {

    baseSuccessApiResponseAssertion(response);
    assertNotNull(response.getData());
  }

  protected void baseSuccessApiListResponseAssertion(ApiListResponse response, ApiListResponse expectation) {

    baseSuccessApiResponseAssertion(response);
    assertNotNull(response.getContent());
    assertNotNull(response.getPageMetaData());
    assertEquals(response.getPageMetaData(), expectation.getPageMetaData());
  }

  protected <T> void successApiListResponseContentAssertion(List<T> response, List<T> expectation) {

    assertFalse(response.isEmpty());
    assertEquals(expectation.size(), response.size());

    for (int i = 0; i < response.size(); i++) {
      assertThat(expectation.get(i)).usingRecursiveComparison()
          .ignoringFields(ignoredFields.toArray(new String[0]))
          .isEqualTo(expectation.get(i));
    }
  }

  private void baseSuccessApiResponseAssertion(ApiResponse response) {

    HttpStatus httpStatusOK = HttpStatus.OK;

    assertNotNull(response);
    assertEquals(httpStatusOK.name(), response.getStatus());
    assertEquals(httpStatusOK.value(), response.getCode());
    assertNull(response.getError());
  }

  protected void baseErrorApiResponseAssertion(HttpStatus errorHttStatus, ApiResponse response) {

    assertNotNull(response);
    assertEquals(errorHttStatus.name(), response.getStatus());
    assertEquals(errorHttStatus.value(), response.getCode());
    assertNotNull(response.getError());
  }
}
