package cn.domob.android.download;

import java.io.File;

public interface DownloadListener {
	/**
	 * 开始下载数据回调，运行在子线程
	 * 
	 * @param url
	 */
	void onStart(String url,String realUrl);

	/**
	 * 下载进度回调，运行在子线程
	 * 
	 * @param current
	 *            当前已下载的大小
	 * @param count
	 *            文件总大小
	 */
	void onLoading(long current, long count);

	/**
	 * 下载完成回调，运行在子线程
	 * 
	 * @param url
	 * @param contentType
	 *            下载文件的类型<br>
	 *            注意：contentType，有可能为空，使用前请进行非空判断
	 * @param f
	 *            下载完成的目标文件
	 */
	void onSuccess(String url,String realUrl, String contentType, File f);

	/**
	 * 下载失败回调，运行在子线程<br>
	 * 注意：当调用暂停方法暂停下载时，根据网络状况会有不同程度延迟
	 * @param error
	 */
	void onFailure(DownloadError error);
}
