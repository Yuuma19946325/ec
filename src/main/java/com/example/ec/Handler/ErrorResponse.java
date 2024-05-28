package com.example.ec.Handler;

import io.micrometer.common.util.StringUtils;
import lombok.Data;

@Data
public class ErrorResponse {

	// レスポンス結果(true:異常あり,false:異常なし)
	private boolean result;
	// ステータスコード
	private int status;
	// エラーメッセージ
	private String message;

	public ErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;

		// エラーメッセージが存在する場合、レスポンス結果をtrueにする
		if (StringUtils.isNotEmpty(message))
			this.result = true;
	}
}
