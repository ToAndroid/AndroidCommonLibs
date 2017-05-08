package cn.domob.android.download;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import cn.domob.android.download.DownloadError.ErrorType;
import cn.domob.android.utils.FileUtility;
import cn.domob.android.utils.IOUtils;
import cn.domob.android.utils.Logger;
import cn.domob.android.utils.NetUtils;

/**
 * 下载线程
 * 
 * @author Alan
 */
class DownloadTheard implements Runnable {

	private static Logger mLogger = new Logger(DownloadTheard.class.getSimpleName());
	// 最初下载地址
	private String mStrUrl;
	// 最终下载地址
	private String mRealUrl;
	private String mFilePath;
	private String mFileName;
	private Proxy mProxy;
	private DownloadListener mDownloadLintener;
	private long mStartIndex = 0;
	private long mEndIndex = 0;
	private URL mUrl = null;
	private File mTargetFile;
	private Context mAppContext;
	private boolean mIsStop = false;
	private boolean mForceDownload = false;
	private String mContentType;
	private int mResponseCode;
	private String mGroup;
	private boolean is302 = false;

	public DownloadTheard(Context appContext, String url, String path, String fileName, boolean forceDownload,
			String group, DownloadListener downloadLintener) {
		this.mForceDownload = forceDownload;
		this.mAppContext = appContext;
		this.mStrUrl = url;
		this.mFilePath = path;
		this.mFileName = fileName;
		this.mGroup = group;
		this.mDownloadLintener = downloadLintener;
	}

	// 停止下载
	public void stopLoad() {
		mIsStop = true;
	}

	@Override
	public void run() {
		boolean isConnect = NetUtils.isWIFI(mAppContext);
		if (!isConnect) {
			isConnect = NetUtils.isMobile(mAppContext);
			if (isConnect) {
				mProxy = NetUtils.readAPN(mAppContext);
			}
		}
		if (isConnect) {
			if (existSDCard()) {
				if (!TextUtils.isEmpty(mStrUrl)) {
					try {
						mUrl = new URL(getDirectUrl(mAppContext,mStrUrl,12));
					} catch (MalformedURLException e) {
						e.printStackTrace();
						mLogger.printStackTrace(e);
						mLogger.errorLog(mStrUrl + "url error!");
						toCallFailure(ErrorType.URL_ERROR);
						return;
					}
					if (mUrl != null) {
						HttpURLConnection mConnection = null;
						try {
							if (mProxy != null) {
								mConnection = (HttpURLConnection) mUrl.openConnection(mProxy);
							} else {
								mConnection = (HttpURLConnection) mUrl.openConnection();
							}

							//设置信任所有证书
							if(mConnection instanceof HttpsURLConnection){
								SSLContext sc = SSLContext.getInstance("SSL");
								sc.init(null, new TrustManager[] { new HttpsUtil.TrustAnyTrustManager() },
										new java.security.SecureRandom());
								((HttpsURLConnection)mConnection).setSSLSocketFactory(sc.getSocketFactory());
								((HttpsURLConnection)mConnection).setHostnameVerifier(new HttpsUtil.TrustAnyHostnameVerifier());
							}

							mConnection.setRequestMethod("GET");
							mConnection.setReadTimeout(DownLoadManager.mReadTimeout);
							mConnection.setConnectTimeout(DownLoadManager.mConnTimeout);
							mContentType = mConnection.getContentType();
							mResponseCode = mConnection.getResponseCode();
							mRealUrl = mConnection.getURL().toString();
							if (!mStrUrl.equals(mRealUrl)) {
								is302 = true;
								String oldUrl = DownLoadManager.getInstance().getSchemeAndHost(mStrUrl);
								DownLoadManager.getInstance().replaceLoadUrlKey(oldUrl, mStrUrl);
							}
							mLogger.debugLog("contentType = " + mContentType);
							mLogger.debugLog("realUrl = " + mRealUrl);
							mLogger.debugLog("responseCode error  code = " + mResponseCode + "!");
						} catch (Exception e) {
							e.printStackTrace();
							mLogger.printStackTrace(e);
							mLogger.errorLog("url.openConnection error!");
							toCallFailure(ErrorType.NETWORK_ERROR);
							e.printStackTrace();
							return;
						}
					//	Log.e("----->" + "DownloadTheard", "run:" + mResponseCode);
						if (mResponseCode >= 200 && mResponseCode < 300) {
							long length = mConnection.getContentLength();
							if (length > 0) {
								mTargetFile = createFile();
								if (mTargetFile != null) {
									mStartIndex = mTargetFile.length();
									long load_size = length - mStartIndex;
									if (load_size == 0) {
										mLogger.errorLog("download file is already exist!");
										toCallFailure(ErrorType.DOWNLOADFILE_EXISTING);
									} else if (load_size > 0) {
										if (checkSDSizeIsEnough(load_size)) {
											mEndIndex = length;
											startDownload();
										} else {
											mLogger.errorLog("SdCard freeSize lack!");
											toCallFailure(ErrorType.FREESPACE_LACK);
										}
									} else {
										mTargetFile.delete();
										mStartIndex = 0;
										mEndIndex = length;
										if (!mTargetFile.exists()) {
											if (!mIsStop) {
												startDownload();
											} else {
												mLogger.errorLog("user stop load!");
												toCallFailure(ErrorType.STOP_DOWNLOAD);
											}
										}
									}
								} else {
									toCallFailure(ErrorType.CREATEFILE_ERROR);
								}
							} else {
								mLogger.errorLog("service's file is error!");
								toCallFailure(ErrorType.SERVICE_ERROR);
							}
						} else {
							toCallFailure(ErrorType.SERVICE_ERROR);
						}
					}
				} else {
					mLogger.errorLog("url is null!");
					toCallFailure(ErrorType.URL_ERROR);
				}
			} else {
				mLogger.errorLog("SDCard unmounted!");
				toCallFailure(ErrorType.SDCARD_UNMOUNTED);
			}
		} else {
			mLogger.errorLog("current net work is not connect!");
			toCallFailure(ErrorType.NETWORK_ERROR);
		}
	}

	/**
	 * 获取重定向或者真正的url
	 * @param context
	 * @param link
	 * @param deep 如果有重定向，deep代表可以重定向的深度
	 * @return
	 */
	private   String getDirectUrl(Context context,String link,int deep) {
		//Log.e("----->" + "DUtils", "getDirectUrl:剩余深度：" +deep );
		if(deep<=0){
			//如果跳转深度大于设置的深度，将不再处理
		//	Log.e("----->" + "DUtils", "getDirectUrl:" + "bigger than deep-limited");
			return null;
		}
		String resultUrl = link;
		HttpURLConnection connection = null;
		try {
			// 如果有代理则使用代理(ctwap、wifi 不检查)
			if ( mProxy!= null) {
				connection = (HttpURLConnection) new URL(link).openConnection(mProxy);
			} else {
				connection = (HttpURLConnection) new URL(link).openConnection();
			}
			//设置信任所有证书
			if(connection instanceof HttpsURLConnection){
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, new TrustManager[] { new cn.domob.android.download.HttpsUtil.TrustAnyTrustManager() },
						new java.security.SecureRandom());
				((HttpsURLConnection)connection).setSSLSocketFactory(sc.getSocketFactory());
				((HttpsURLConnection)connection).setHostnameVerifier(new cn.domob.android.download.HttpsUtil.TrustAnyHostnameVerifier());
			}
			connection.setInstanceFollowRedirects(false);
			connection.connect();
			int responseCode = connection.getResponseCode();
		//	Log.e("----->" + "DUtils", "getDirectUrl:返回的请求码：" + responseCode);
			if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP||responseCode==HttpURLConnection.HTTP_SEE_OTHER) {
				String locationUrl = connection.getHeaderField("Location");
			//	Log.e("----->" + "DUtils", "getDirectUrl:需要重定向，重定向的url：" +locationUrl );
				if (locationUrl != null && locationUrl.trim().length() > 0) {
					resultUrl = getDirectUrl(context,locationUrl,--deep);
				}
			}
		} catch (Exception e) {
			Log.e("----->" + "dloadthread", "getDirectUrl:" + e.toString());
		} finally {
			if(connection!=null)
				connection.disconnect();
		}
		return resultUrl;
	}

	private void toCallFailure(ErrorType errorType) {
		DownloadError downloadError = new DownloadError(errorType);
		downloadError.setUrl(mStrUrl);
		downloadError.setFile(mTargetFile);
		downloadError.setResponseCode(mResponseCode);
		callFailure(downloadError);
	}

	private void startDownload() {
		ThreadPoolExecutor threadPool = DownLoadManager.getInstance().getThreadPool();
		if (threadPool != null && !threadPool.isShutdown()) {
			DownLoadTask loadTask = new DownLoadTask();
			threadPool.submit(loadTask);
		}
	}

	private class DownLoadTask implements Runnable {
		@Override
		public void run() {
			RandomAccessFile raf = null;
			InputStream is = null;
			HttpURLConnection mConnection = null;
			try {
				if (mProxy != null) {
					mConnection = (HttpURLConnection) mUrl.openConnection(mProxy);
				} else {
					mConnection = (HttpURLConnection) mUrl.openConnection();
				}
				//设置信任所有证书
				if(mConnection instanceof HttpsURLConnection){
					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, new TrustManager[] { new HttpsUtil.TrustAnyTrustManager() },
							new java.security.SecureRandom());
					((HttpsURLConnection)mConnection).setSSLSocketFactory(sc.getSocketFactory());
					((HttpsURLConnection)mConnection).setHostnameVerifier(new HttpsUtil.TrustAnyHostnameVerifier());
				}

				mConnection.setReadTimeout(DownLoadManager.mReadTimeout);
				mConnection.setConnectTimeout(DownLoadManager.mConnTimeout);
				mConnection.setRequestMethod("GET");
				// range 范围
				mConnection.setRequestProperty("Range", "bytes=" + mStartIndex + "-" + mEndIndex);
				mResponseCode = mConnection.getResponseCode();
			} catch (Exception e) {
				mLogger.printStackTrace(e);
				mLogger.debugLog("url.openConnection() error!");
				toCallFailure(ErrorType.UNDEFINED_ERROR);
				return;
			}
			if (mResponseCode >= 200 && mResponseCode < 300) {
				try {
					raf = new RandomAccessFile(mTargetFile, "rwd");
				} catch (FileNotFoundException e) {
					Log.e("----->" + "DTask", "run:" + e.toString());
					mLogger.printStackTrace(e);
					mLogger.debugLog("file not found error!");
					toCallFailure(ErrorType.CREATEFILE_ERROR);
					return;
				}
				try {
					raf.seek(mStartIndex);
					is = mConnection.getInputStream();
					int len = 0;
					byte[] buffer = new byte[DownLoadManager.mBufferSize];
					callStart(mStrUrl, mRealUrl);
					callProgress(mStartIndex, mEndIndex);
					while ((len = is.read(buffer)) != -1) {
						raf.write(buffer, 0, len);
						mStartIndex += len;
						callProgress(mStartIndex, mEndIndex);
						if (mIsStop) {
							raf.close();
							is.close();
							toCallFailure(ErrorType.STOP_DOWNLOAD);
							return;
						}
					}
				} catch (IOException e) {
					mLogger.printStackTrace(e);
					mLogger.debugLog("raf.seek(startIndex) is error!");
					IOUtils.closeStream(raf);
					IOUtils.closeStream(is);
					toCallFailure(ErrorType.NETWORK_ERROR);
					return;
				}
				mLogger.debugLog("文件下载成功！");
				IOUtils.closeStream(raf);
				IOUtils.closeStream(is);
				recordDownloadInfo();
				callSuccess(mStrUrl, mRealUrl, mTargetFile);
			} else {
				mLogger.errorLog("responseCode error  code = " + mResponseCode + "!");
				toCallFailure(ErrorType.SERVICE_ERROR);
			}
		}
	}

	/**
	 * 记录下载信息
	 */
	private void recordDownloadInfo() {
		if (mTargetFile != null) {
			final String fileMD5Str = FileUtility.getFileMD5(mTargetFile);
			if (mAppContext != null && !TextUtils.isEmpty(fileMD5Str)
					&& !TextUtils.isEmpty(mTargetFile.getAbsolutePath())) {
				new Thread() {
					public void run() {
						DownLoadManager.saveProperties(mAppContext, fileMD5Str, mTargetFile.getAbsolutePath(),
								mContentType);
					}
				}.start();
			}
		}
	}

	// 开始下载回调
	private void callStart(String url, String realUrl) {
		if (mDownloadLintener != null) {
			mDownloadLintener.onStart(url, realUrl);
		}
	}

	// 下载进度回调
	private void callProgress(long count, long current) {
		if (mDownloadLintener != null) {
			mDownloadLintener.onLoading(count, current);
		}
	}

	// 回调下载成功 将标记mIsStop至为true 并移除DownLoadManager的正在下载记录
	private void callSuccess(String url, String realUrl, File tagerFile) {
		mIsStop = true;
		DownLoadManager.getInstance().removeLoadingUrl(mStrUrl, mGroup, is302);
		if (mDownloadLintener != null) {
			mDownloadLintener.onSuccess(url, realUrl, mContentType, tagerFile);
		}
	}

	// 回调下载失败 将标记mIsStop至为true 并移除DownLoadManager的正在下载记录
	private void callFailure(DownloadError errorStr) {
		mIsStop = true;
		DownLoadManager.getInstance().removeLoadingUrl(mStrUrl, mGroup, is302);
		if (mDownloadLintener != null)
			mDownloadLintener.onFailure(errorStr);
	}

	/**
	 * 获取下载的文件的名称
	 * 
	 * @param path
	 * @return
	 */
	private String getFileName(String path) {
		// 返回32位的MD5值
		String md5Str = FileUtility.getMD5Str(path);
		return md5Str;
	}

	// 检测sd卡剩余空间是否足够
	private boolean checkSDSizeIsEnough(long load_size) {
		long sdFreeSize = FileUtility.getSDFreeSize();
		if (sdFreeSize > load_size) {
			return true;
		}
		return false;
	}

	// 判断sd卡是否存在
	private boolean existSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	// 创建下载目标文件 创建失败返回null
	private File createFile() {
		if (TextUtils.isEmpty(mFilePath)) {
			mFilePath = DownLoadManager.getDownloadPath() + mAppContext.getPackageName() + "/";
		}
		if (TextUtils.isEmpty(mFileName)) {
			String nameUrl;
			if (is302) {
				nameUrl = mStrUrl;
			} else {
				nameUrl = DownLoadManager.getInstance().getSchemeAndHost(mStrUrl);
			}
			mFileName = getFileName(nameUrl);
		}
		File file = null;
		try {
			File dir_file = new File(mFilePath);
			if (!dir_file.exists()) {
				dir_file.mkdirs();
			}
			file = new File(mFilePath, mFileName);
			if (!file.exists()) {
				if (file.createNewFile()) {
					return file;
				} else {
					return null;
				}
			} else {
				if (mForceDownload) {
					file.delete();
					file.createNewFile();
				}
			}
		} catch (IOException e) {
			Log.e("----->" + "download", "createFile:" + e.toString());
			mLogger.printStackTrace(e);
			mLogger.errorLog("create file failure");
		}
		return file;
	}
}
