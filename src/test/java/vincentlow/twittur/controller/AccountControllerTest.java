package vincentlow.twittur.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import vincentlow.twittur.model.constant.ApiPath;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.request.AccountRelationshipRequest;
import vincentlow.twittur.model.request.UpdateAccountEmailRequest;
import vincentlow.twittur.model.request.UpdateAccountPasswordRequest;
import vincentlow.twittur.model.request.UpdateAccountPhoneNumberRequest;
import vincentlow.twittur.model.request.UpdateAccountRequest;
import vincentlow.twittur.service.AccountService;

public class AccountControllerTest {

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 10;

  private final PageRequest PAGE_REQUEST = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  private final String USERNAME = "USERNAME";

  private final String ACCOUNT_NAME = "ACCOUNT_NAME";

  private final String BIO = "BIO";

  private final String FOLLOWER_ID = "FOLLOWER_ID";

  private final String FOLLOWED_ID = "FOLLOWED_ID";

  private final int TWEETS_COUNT = 0;

  private final int FOLLOWERS_COUNT = 0;

  private final int FOLLOWING_COUNT = 0;

  @InjectMocks
  private AccountController accountController;

  @Mock
  private AccountService accountService;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  private Map<String, String> params;

  private MultiValueMap<String, String> multiValueParams;

  private HttpStatus httpStatus;

  private Account account;

  private List<Account> accountList;

  private Page<Account> accountPage;

  private UpdateAccountRequest updateAccountRequest;

  private UpdateAccountEmailRequest updateAccountEmailRequest;

  private UpdateAccountPhoneNumberRequest updateAccountPhoneNumberRequest;

  private UpdateAccountPasswordRequest updateAccountPasswordRequest;

  private AccountRelationshipRequest accountRelationshipRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);
    mockMvc = standaloneSetup(accountController).build();

    objectMapper = new ObjectMapper();

    httpStatus = HttpStatus.OK;

    account = new Account();
    account.setUsername(USERNAME);
    account.setAccountName(ACCOUNT_NAME);
    account.setBio(BIO);

    accountList = new ArrayList<>();
    accountList.add(account);

    accountPage = new PageImpl<>(accountList, PAGE_REQUEST, accountList.size());

    updateAccountRequest = UpdateAccountRequest.builder()
        .build();

    updateAccountEmailRequest = UpdateAccountEmailRequest.builder()
        .build();

    updateAccountPhoneNumberRequest = UpdateAccountPhoneNumberRequest.builder()
        .build();

    updateAccountPasswordRequest = UpdateAccountPasswordRequest.builder()
        .build();

    accountRelationshipRequest = AccountRelationshipRequest.builder()
        .followerId(FOLLOWER_ID)
        .followedId(FOLLOWED_ID)
        .build();

    params = new HashMap<>();
    params.put("pageNumber", String.valueOf(PAGE_NUMBER));
    params.put("pageSize", String.valueOf(PAGE_SIZE));

    multiValueParams = new LinkedMultiValueMap<>();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      multiValueParams.add(entry.getKey(), entry.getValue());
    }

    when(accountService.findAccounts(PAGE_NUMBER, PAGE_SIZE)).thenReturn(accountPage);
    when(accountService.findAccountByUsername(USERNAME)).thenReturn(account);
    doNothing().when(accountService)
        .updateAccountByUsername(eq(USERNAME), any(UpdateAccountRequest.class));
    doNothing().when(accountService)
        .updateAccountEmailAddressByUsername(eq(USERNAME), any(UpdateAccountEmailRequest.class));
    doNothing().when(accountService)
        .updateAccountPhoneNumberByUsername(eq(USERNAME), any(UpdateAccountPhoneNumberRequest.class));
    doNothing().when(accountService)
        .updateAccountPasswordByUsername(eq(USERNAME), any(UpdateAccountPasswordRequest.class));
    doNothing().when(accountService)
        .follow(any(AccountRelationshipRequest.class));
    doNothing().when(accountService)
        .unfollow(any(AccountRelationshipRequest.class));
    when(accountService.getAccountFollowers(USERNAME, PAGE_NUMBER, PAGE_SIZE)).thenReturn(accountPage);
    when(accountService.getAccountFollowing(USERNAME, PAGE_NUMBER, PAGE_SIZE)).thenReturn(accountPage);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(accountService);
  }

  @Test
  void getAccounts() throws Exception {

    this.mockMvc.perform(get(ApiPath.ACCOUNT).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .queryParams(multiValueParams))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.content[0].username", equalTo(USERNAME)))
        .andExpect(jsonPath("$.content[0].accountName", equalTo(ACCOUNT_NAME)))
        .andExpect(jsonPath("$.content[0].bio", equalTo(BIO)))
        .andExpect(jsonPath("$.content[0].tweetsCount", equalTo(TWEETS_COUNT)))
        .andExpect(jsonPath("$.content[0].followersCount", equalTo(FOLLOWERS_COUNT)))
        .andExpect(jsonPath("$.content[0].followingCount", equalTo(FOLLOWING_COUNT)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(accountService).findAccounts(PAGE_NUMBER, PAGE_SIZE);
  }

  @Test
  void getAccountByUsername() throws Exception {

    this.mockMvc.perform(get(ApiPath.ACCOUNT + "/@" + USERNAME).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.data.username", equalTo(USERNAME)))
        .andExpect(jsonPath("$.data.accountName", equalTo(ACCOUNT_NAME)))
        .andExpect(jsonPath("$.data.bio", equalTo(BIO)))
        .andExpect(jsonPath("$.data.tweetsCount", equalTo(TWEETS_COUNT)))
        .andExpect(jsonPath("$.data.followersCount", equalTo(FOLLOWERS_COUNT)))
        .andExpect(jsonPath("$.data.followingCount", equalTo(FOLLOWING_COUNT)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(accountService).findAccountByUsername(USERNAME);
  }

  @Test
  void updateAccount() throws Exception {

    this.mockMvc.perform(put(ApiPath.ACCOUNT + "/@" + USERNAME).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateAccountRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(accountService).updateAccountByUsername(USERNAME, updateAccountRequest);
  }

  @Test
  void updateAccount_emailAddress() throws Exception {

    this.mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + USERNAME + "/emailAddress").accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateAccountEmailRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(accountService).updateAccountEmailAddressByUsername(USERNAME, updateAccountEmailRequest);
  }

  @Test
  void updateAccount_phoneNumber() throws Exception {

    this.mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + USERNAME + "/phoneNumber").accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateAccountPhoneNumberRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(accountService).updateAccountPhoneNumberByUsername(USERNAME, updateAccountPhoneNumberRequest);
  }

  @Test
  void updateAccount_password() throws Exception {

    this.mockMvc.perform(put(ApiPath.ACCOUNT + "/@" + USERNAME + "/password").accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateAccountPasswordRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(accountService).updateAccountPasswordByUsername(USERNAME, updateAccountPasswordRequest);

  }

  @Test
  void initDummyAccounts() throws Exception {

    this.mockMvc.perform(post(ApiPath.ACCOUNT + "/init").accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateAccountRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(accountService).initDummyAccounts();
  }

  @Test
  void followAccount() throws Exception {

    this.mockMvc.perform(post(ApiPath.ACCOUNT + "/_follow").accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(accountRelationshipRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(accountService).follow(accountRelationshipRequest);
  }

  @Test
  void unfollowAccount() throws Exception {

    this.mockMvc.perform(post(ApiPath.ACCOUNT + "/_unfollow").accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(accountRelationshipRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(accountService).unfollow(accountRelationshipRequest);
  }

  @Test
  void getAccountFollowers() throws Exception {

    this.mockMvc.perform(get(ApiPath.ACCOUNT + "/@" + USERNAME + "/followers").accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .queryParams(multiValueParams))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.content[0].username", equalTo(USERNAME)))
        .andExpect(jsonPath("$.content[0].accountName", equalTo(ACCOUNT_NAME)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(accountService).getAccountFollowers(USERNAME, PAGE_NUMBER, PAGE_SIZE);
  }

  @Test
  void getAccountFollowing() throws Exception {

    this.mockMvc.perform(get(ApiPath.ACCOUNT + "/@" + USERNAME + "/following").accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .queryParams(multiValueParams))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.content[0].username", equalTo(USERNAME)))
        .andExpect(jsonPath("$.content[0].accountName", equalTo(ACCOUNT_NAME)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(accountService).getAccountFollowing(USERNAME, PAGE_NUMBER, PAGE_SIZE);
  }
}
