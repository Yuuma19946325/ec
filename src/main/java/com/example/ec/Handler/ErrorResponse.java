package com.example.ec.Handler;

import io.micrometer.common.util.StringUtils;

public class ErrorResponse {
	
	// レスポンス結果(true:異常あり,false:異常なし)
	private boolean result;
	// ステータスコード
    private int status;
    // エラーメッセージ
    private String message;

    public ErrorResponse(int status,String message) {
        this.status = status;
        this.message = message;
        
        // エラーメッセージが存在する場合、レスポンス結果をtrueにする
        if(StringUtils.isNotEmpty(message)) this.result = true;
    }

	/**
	 * @return result
	 */
	public boolean isResult() {
		return result;
	}

	/**
	 * @param result セットする result
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

	/**
	 * @return status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status セットする status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message セットする message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
