package cn.domob.android.download;

import java.io.File;

public class DownloadError {

	public enum ErrorType {

		REPEAT_DOWNLOAD("当前文件正在下载，请勿重复下载！"), NETWORK_ERROR("当前网络不可用，请检查网络设置！"), SDCARD_UNMOUNTED(
				"sd卡没有挂载，请检查SDCard是否正常！"), URL_ERROR("当前url异常，请检查url是否正确！ "), STOP_DOWNLOAD("取消下载任务！"), FREESPACE_LACK(
				"sd卡剩余空间不足！"), DOWNLOADFILE_EXISTING("需要下载的文件已经存在！"), CREATEFILE_ERROR("本地下载的目标文件创建失败！"), SERVICE_ERROR(
				"服务器端错误！"), CONTEXT_ERROR("context不能为空！"),UNDEFINED_ERROR("未定义错误！");
		private ErrorType(String msg) {
			this.msg = msg;
		}
		private final String msg;

		public String getErrorMessage() {
			return this.msg;
		}
	}

	private int mResponseCode;
	private String mMessage;
	private File mFile;
	private String mStrUrl;
	private ErrorType mErrorType;

	public DownloadError(ErrorType errorType) {
		this.mMessage = errorType.getErrorMessage();
		this.mErrorType = errorType;
	}

	public ErrorType getErrorType() {
		return mErrorType;
	}
	
	public int getResponseCode() {
		return mResponseCode;
	}

	public void setResponseCode(int responseCode) {
		this.mResponseCode = responseCode;
	}

	public String getMessage() {
		return mMessage;
	}
	/**
	 * 注意 使用前进行非空判断
	 * @return file
	 */
	public File getFile() { 
		return mFile;
	}

	public void setFile(File file) {
		this.mFile = file;
	}

	public String getUrl() {
		return mStrUrl;
	}

	public void setUrl(String url) {
		this.mStrUrl = url;
	}
}
