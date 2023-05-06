package vincentlow.twittur.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.model.wrapper.PageMetaData;

public class BaseControllerTest {

  private final String ERROR = "ERROR";

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 10;

  private final PageRequest PAGE_REQUEST = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  @InjectMocks
  private BaseController baseController;

  private Account account;

  private List<Account> accountList;

  private Page<Account> accountPage;

  private PageMetaData pageMetaData;

  @BeforeEach
  void setUp() {

    openMocks(this);

    account = new Account();

    accountList = new ArrayList<>();
    accountList.add(account);

    accountPage = new PageImpl<>(accountList, PAGE_REQUEST, accountList.size());

    pageMetaData = new PageMetaData();
    pageMetaData.setPageNumber(PAGE_NUMBER);
    pageMetaData.setPageSize(PAGE_SIZE);
    pageMetaData.setTotalRecords(accountPage.getTotalElements());
  }

  @AfterEach
  void tearDown() {}

  @Test
  void toSuccessApiResponse_apiListResponse() {

    ApiListResponse result = baseController.toApiListResponse(accountList, pageMetaData);

    assertNotNull(result);
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
  }

  @Test
  void toSuccessApiResponse_apiSingleResponse() {

    ApiSingleResponse result = baseController.toApiSingleResponse(account);

    assertNotNull(result);
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
  }

  @Test
  void toErrorApiResponse() {

    ApiResponse result = baseController.toErrorApiResponse(HttpStatus.BAD_REQUEST, ERROR);

    assertNotNull(result);
    assertEquals(HttpStatus.BAD_REQUEST.value(), result.getCode());
    assertEquals(HttpStatus.BAD_REQUEST.name(), result.getStatus());
    assertEquals(ERROR, result.getError());
  }

  @Test
  void getPageMetaData() {

    PageMetaData result = baseController.getPageMetaData(accountPage, PAGE_NUMBER, PAGE_SIZE);

    assertNotNull(result);
    assertEquals(PAGE_NUMBER, result.getPageNumber());
    assertEquals(PAGE_SIZE, result.getPageSize());
    assertTrue(result.getTotalRecords() > 0);
  }
}
