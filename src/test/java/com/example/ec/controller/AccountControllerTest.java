package com.example.ec.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.ec.CommonTest;
import com.example.ec.Handler.BadRequestException;
import com.example.ec.Handler.ErrorResponse;
import com.example.ec.Handler.SQLException;
import com.example.ec.entity.Account;
import com.example.ec.service.AccountService;

@WebMvcTest(AccountController.class)
public class AccountControllerTest extends CommonTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private AccountService accountService;

	@Test
	@DisplayName("アカウント作成API_OK")
	public void createAccount_OK() throws Exception {

		final Account accountDetails = new Account("小林", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);

		// モックの設定
		doNothing().when(accountService).checkAccountData(accountDetails);
		doNothing().when(accountService).createAccount(accountDetails);

		mvc.perform(MockMvcRequestBuilders.post("/api/account")
				.content(asJsonString(accountDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("アカウント作成API_入力情報NG")
	public void createAccount_NG1() throws Exception {

		final Account accountDetails = new Account(null, "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"アカウント名が未入力です");

		// モックの設定
		doThrow(new BadRequestException("アカウント名が未入力です")).when(accountService).checkAccountData(accountDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/account")
				.content(asJsonString(accountDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("アカウント作成API_存在チェック取得NG")
	public void createAccount_NG2() throws Exception {

		final Account accountDetails = new Account("小林", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"アカウントの取得に失敗しました");

		// モックの設定
		doNothing().when(accountService).checkAccountData(accountDetails);
		doThrow(new SQLException("アカウントの取得に失敗しました")).when(accountService)
				.checkExistsAccount(accountDetails.getMailAddress());

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/account")
				.content(asJsonString(accountDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("アカウント作成API_存在チェックNG")
	public void createAccount_NG3() throws Exception {

		final Account accountDetails = new Account("小林", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"このメールアドレスは既に存在しています");

		// モックの設定
		doNothing().when(accountService).checkAccountData(accountDetails);
		doThrow(new SQLException("このメールアドレスは既に存在しています")).when(accountService)
				.checkExistsAccount(accountDetails.getMailAddress());

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/account")
				.content(asJsonString(accountDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("アカウント作成API_作成NG")
	public void createAccount_NG4() throws Exception {

		final Account accountDetails = new Account("小林", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"アカウントの作成に失敗しました");

		// モックの設定
		doNothing().when(accountService).checkAccountData(accountDetails);
		doNothing().when(accountService).checkAccountData(accountDetails);
		doThrow(new SQLException("アカウントの作成に失敗しました")).when(accountService).createAccount(accountDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/account")
				.content(asJsonString(accountDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("アカウント読込API_OK")
	public void getAccount_OK() throws Exception {

		Long accountId = (long) 1;

		final Account accountDetails = new Account("小林", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		// モックの設定
		doReturn(accountDetails).when(accountService).getAccount(accountId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/account/" + accountId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(accountDetails)));
	}

	@Test
	@DisplayName("アカウント読込API_NG")
	public void getAccount_NG() throws Exception {

		Long accountId = (long) 2;

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"アカウントの取得に失敗しました");

		// モックの設定
		doThrow(new SQLException("アカウントの取得に失敗しました")).when(accountService).getAccount(accountId);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.get("/api/account/" + accountId)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("アカウント更新API_OK")
	public void updataAccount_OK() throws Exception {

		Long accountId = (long) 1;
		final Account accountDetails = new Account("小林", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.CREATED.value(),
				null);

		// モックの設定
		doNothing().when(accountService).checkAccountData(accountDetails);
		doNothing().when(accountService).updateAccount(accountId, accountDetails);

		mvc.perform(MockMvcRequestBuilders.put("/api/account/" + accountId)
				.content(asJsonString(accountDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(asJsonString(errorResponse)));

	}

	@Test
	@DisplayName("アカウント更新API_入力情報NG")
	public void updataAccount_NG1() throws Exception {

		final Account accountDetails = new Account(null, "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"アカウント名が未入力です");

		// モックの設定
		doThrow(new BadRequestException("アカウント名が未入力です")).when(accountService).checkAccountData(accountDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/account")
				.content(asJsonString(accountDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().json(asJsonString(errorResponse)));
	}

	@Test
	@DisplayName("アカウント更新API_作成NG")
	public void updataAccount_NG2() throws Exception {

		final Account accountDetails = new Account("小林", "3380014", "埼玉県", "080", "yuuma19946325@gmail.com",
				"19946325Yuuma");

		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"アカウントの更新に失敗しました");

		// モックの設定
		doNothing().when(accountService).checkAccountData(accountDetails);
		doThrow(new SQLException("アカウントの更新に失敗しました")).when(accountService).createAccount(accountDetails);

		// テスト実行
		mvc.perform(MockMvcRequestBuilders.post("/api/account")
				.content(asJsonString(accountDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(asJsonString(errorResponse)));
	}
}
