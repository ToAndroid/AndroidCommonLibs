package cn.domob.android.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.Properties;

import cn.domob.android.utils.FileUtility;
import cn.domob.android.utils.IOUtils;
import cn.domob.android.utils.Logger;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 单列，下载模块对外暴露类，提供下载，暂停方法。
 * 
 * @author Alan
 */
public class DownLoadManager {

	private static Logger mLogger = new Logger(DownLoadManager.class.getSimpleName());
	// 存储正在下载的任务
	private Map<String, DownloadTheard> mLoadingUrl;
	// 连接超时时间
	static int mConnTimeout = 1000 * 20;
	// 读取超时时间
	static int mReadTimeout = 1000 * 40;
	// 下载缓冲区大小
	static int mBufferSize = 1024 * 10;
	// properties的文件提示
	private static final String PROPERTIES_SAVE_COMMENT = "Important files mistakenly deleted ！";
	// 下载的根目录
	private static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/SystemFileStorage/";
	// 保存下载信息md5值的文件名
	private static final String PROTMD5_NAME = "md5.properties";
	// 保存下载信息contentType的文件名
	private static final String PROTCONTENTTYPE_NAME = "contentType.properties";
	// 最大的线程数
	private static final int MAC_THREAD = 20;
	// 存储已经下载好的文件MD5值和路径
	private HashMap<String, String> mMD5Infos;
	// 存储已经下载好的文件MD5值和contentType
	private HashMap<String, String> mTypeInfos;
	// 线程池
	private ThreadPoolExecutor mThreadPool;
	// 下载的组
	private HashMap<String, List<String>> mGroups;
	// 用于表示是否初始化完毕
	private boolean mIsInitDone = false;
	// 下载线程
	private List<Thread> mDownloadTreads;

	private DownLoadManager() {
		mThreadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		mThreadPool.setMaximumPoolSize(MAC_THREAD);
		mMD5Infos = new HashMap<String, String>();
		mTypeInfos = new HashMap<String, String>();
		mLoadingUrl = new HashMap<String, DownloadTheard>();
		initDownloadInfo();
	}

	public static DownLoadManager getInstance() {
		return DownLoadManagerHoleder.instance;
	}

	private static class DownLoadManagerHoleder {
		private static DownLoadManager instance = new DownLoadManager();
	}

	// 初始化 加载已下载信息
	private void initDownloadInfo() {
		new Thread() {
			public void run() {
				File rootfile = new File(DOWNLOAD_PATH);
				if (rootfile.isDirectory()) {
					File[] listFiles = rootfile.listFiles();
					if (listFiles != null)
						for (int i = 0; i < listFiles.length; i++) {
							if (listFiles[i].isDirectory())
								try {
									loadProts(listFiles[i]);
								} catch (Exception e) {
									mLogger.printStackTrace(e);
									mLogger.errorLog("加载已下载的MD5值失败!");
								}
						}
				}
				mIsInitDone = true;
				if (mDownloadTreads != null && mDownloadTreads.size() > 0) {
					for (Thread thread : mDownloadTreads) {
						thread.start();
					}
					mDownloadTreads.clear();
					mDownloadTreads = null;
				}
			}
		}.start();
	}

	/**
	 * 加载当前文件下的已经下载完成的文件信息到内存
	 * 
	 * @param mFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void loadProts(File mFile) throws FileNotFoundException, IOException {
		if (mFile.isDirectory()) {
			File file = new File(mFile, PROTMD5_NAME);
			File typeFile = new File(mFile, PROTCONTENTTYPE_NAME);
			if (file.isFile() && file.length() > 0) {
				Properties typeProperties = null;
				if (typeFile.isFile() && typeFile.length() > 0) {
					typeProperties = new Properties();
					FileInputStream typeInputStream = new FileInputStream(typeFile);
					typeProperties.load(typeInputStream);
					typeInputStream.close();
				}
				Properties md5Properties = new Properties();
				FileInputStream md5InputStream = new FileInputStream(file);
				md5Properties.load(md5InputStream);
				md5InputStream.close();
				List<String> removeKeys = null;
				for (Entry<Object, Object> temp : md5Properties.entrySet()) {
					File tempFile = new File((String) temp.getValue());
					if (tempFile.isFile()) {
						String key = (String) temp.getKey();
						String value = (String) temp.getValue();
						if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
							mMD5Infos.put(key, value);
							if (typeProperties != null) {
								mTypeInfos.put(key, typeProperties.getProperty(key));
							}
						} else {
							if (removeKeys == null) {
								removeKeys = new ArrayList<String>();
							}
							removeKeys.add((String) temp.getKey());
						}
					} else {
						if (removeKeys == null) {
							removeKeys = new ArrayList<String>();
						}
						removeKeys.add((String) temp.getKey());
					}
				}
				// 有不一致则更新Properties文件
				if (removeKeys != null) {
					for (String key : removeKeys) {
						md5Properties.remove(key);
						if (typeProperties != null) {
							typeProperties.remove(key);
						}
					}
					FileOutputStream outputStream = new FileOutputStream(file);
					md5Properties.store(outputStream, PROPERTIES_SAVE_COMMENT);
					outputStream.close();
					if (typeProperties != null) {
						FileOutputStream typeOutputStream = new FileOutputStream(typeFile);
						md5Properties.store(typeOutputStream, PROPERTIES_SAVE_COMMENT);
						typeOutputStream.close();
					}
				}
			} else if (typeFile.isFile() && typeFile.length() > 0) {
				typeFile.delete();
			}
		}
	}

	/**
	 * 根据文件的MD5值删除对应的文件
	 * 
	 * @param fileMD5
	 */
	public boolean deleteFile(String fileMD5) {
		if (mMD5Infos != null && mMD5Infos.size() > 0) {
			if (!TextUtils.isEmpty(fileMD5)) {
				String dirPath = mMD5Infos.remove(fileMD5);
				File file = new File(dirPath);
				if (file.isFile()) {
					return file.delete();
				}
			}
		}
		return false;
	}

	/**
	 * 获取下载路径根目录
	 * 
	 * @return
	 */
	public static String getDownloadPath() {
		return DOWNLOAD_PATH;
	}

	/**
	 * 获取当前线程池
	 * 
	 * @return
	 */
	public ThreadPoolExecutor getThreadPool() {
		return mThreadPool;
	}

	/**
	 * 设置自己的线程池
	 * 
	 * @param threadPool
	 */
	public void setThreadPool(ThreadPoolExecutor threadPool) {
		this.mThreadPool = threadPool;
	}

	public static int getBufferSize() {
		return mBufferSize;
	}

	/**
	 * 设置下载的缓冲区大小 默认 1024 * 10
	 * 
	 * @param bufferSize
	 */
	public static void setBufferSize(int bufferSize) {
		DownLoadManager.mBufferSize = bufferSize;
	}

	/**
	 * 获取当前连接超时时间
	 * 
	 * @return mConnTimeout
	 */
	public static int getConnTimeout() {
		return mConnTimeout;
	}

	/**
	 * 设置下载连接超时时间 默认 1024 * 20
	 * 
	 * @param connTimeout
	 */
	public static void setConnTimeout(int connTimeout) {
		DownLoadManager.mConnTimeout = connTimeout;
	}

	/**
	 * 获取当前读取超时时间
	 * 
	 * @return mReadTimeout
	 */
	public static int getReadTimeout() {
		return mReadTimeout;
	}

	/**
	 * 设置下载读取超时时间 默认 1024 * 40
	 * 
	 * @param readTimeout
	 */
	public static void setReadTimeout(int readTimeout) {
		DownLoadManager.mReadTimeout = readTimeout;
	}

	/**
	 * 移除mLoadingUrl中的下载任务记录
	 * 
	 *            要移除的任务对应的url
	 * @param group
	 *            要移除任务的对应的租
	 */
	void removeLoadingUrl(String removeUrl, String group, boolean is302) {
		synchronized (this.getClass()) {
			String url = null;
			if (is302) {
				url = removeUrl;
			} else {
				url = getSchemeAndHost(removeUrl);
			}
			mLoadingUrl.remove(url);
			if (!TextUtils.isEmpty(group)) {
				if (mGroups != null && mGroups.size() > 0) {
					List<String> list = mGroups.get(group);
					if (list != null && list.size() > 0) {
						list.remove(url);
						if (list.size() == 0) {
							mGroups.remove(group);
						}
					} else {
						mGroups.remove(group);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param uriString
	 *            下载地址
	 * @return uri.scheme+uri.host or null
	 */
	String getSchemeAndHost(String uriString) {
		if (!TextUtils.isEmpty(uriString)) {
			Uri uri = Uri.parse(uriString);
			String scheme = uri.getScheme();
			String authority = uri.getAuthority();
			String path = uri.getPath();
			return scheme + authority + path;
		}
		return null;
	}

	/**
	 * 记录下载任务，将url对应的Theard存在到mLoadingUrl中
	 * 
	 * @param url
	 * @param theard
	 */
	void addLoadUrl(String url, DownloadTheard theard) {
		synchronized (this.getClass()) {
			String schemeAndHostUrl = getSchemeAndHost(url);
			mLoadingUrl.put(schemeAndHostUrl, theard);
		}
	}

	/**
	 * 替换记录在mLoadingUrl内的key url
	 * 
	 * @param oldUrl
	 * @param newUrl
	 */
	void replaceLoadUrlKey(String oldUrl, String newUrl) {
		synchronized (this.getClass()) {
			if (mLoadingUrl != null && mLoadingUrl.size() > 0) {
				if (!TextUtils.isEmpty(oldUrl) && !TextUtils.isEmpty(newUrl)) {
					DownloadTheard downloadTheard = mLoadingUrl.remove(oldUrl);
					mLoadingUrl.put(newUrl, downloadTheard);
				}
			}
		}
	}

	/**
	 * 取消下载任务
	 * 
	 * @param url
	 */
	public void stopLoad(String url) {
		synchronized (this.getClass()) {
			String schemeAndHostUrl = getSchemeAndHost(url);
			DownloadTheard downloadTheard1 = mLoadingUrl.remove(schemeAndHostUrl);
			DownloadTheard downloadTheard2 = mLoadingUrl.remove(url);
			if (downloadTheard1 != null) {
				downloadTheard1.stopLoad();
				downloadTheard1 = null;
			}
			if (downloadTheard2 != null) {
				downloadTheard2.stopLoad();
				downloadTheard2 = null;
			}
		}
	}

	/**
	 * 取消一组下载任务
	 * 
	 * @param group
	 */
	public void stopForGroup(String group) {
		if (mGroups != null && mGroups.size() > 0) {
			List<String> list = mGroups.remove(group);
			if (list != null && list.size() > 0) {
				for (String url : list) {
					stopLoad(url);
				}
			}
		}
	}

	/**
	 * * 下载文件
	 * 
	 * @param context
	 * @param url
	 *            下载地址
	 * @param targetPath
	 *            文件存储在本地的路径
	 * @param fileName
	 *            文件名
	 * @param forceDownload
	 *            是否强制下载,强制下载，如果文件已存在会先删除
	 * @param md5
	 *            文件MD5值
	 * @param gorup
	 *            下载任务所在组
	 * @param downloadListener
	 *            下载监听
	 */
	public void download(final Context context, final String url, final String targetPath, final String fileName,
			final boolean forceDownload, final String md5, final String gorup, final DownloadListener downloadListener) {
		if (context != null) {
			Thread downloadThread = new Thread() {
				@Override
				public void run() {
					synchronized (DownLoadManager.class) {
						String schemeAndHostUrl = getSchemeAndHost(url);
						if (mLoadingUrl != null && mLoadingUrl.containsKey(schemeAndHostUrl) || mLoadingUrl != null
								&& mLoadingUrl.containsKey(url)) {
							mLogger.errorLog("当前文件正在下载! url = " + url);
							if (downloadListener != null) {
								DownloadError downloadError = new DownloadError(DownloadError.ErrorType.REPEAT_DOWNLOAD);
								downloadError.setUrl(url);
								downloadListener.onFailure(downloadError);
							}
						} else {
							// 判断是否强制下载 是否带MD5值 是否有下载记录
							if (!forceDownload && !TextUtils.isEmpty(md5) && mMD5Infos.containsKey(md5)) {
								try {
									String filePath = (String) mMD5Infos.get(md5);
									File file = new File(filePath);
									if (file.isFile()) {
										String fileMD5 = FileUtility.getFileMD5(file);
										// 再次验证MD5值
										if (md5.equals(fileMD5)) {
											String path;
											String name;
											if (TextUtils.isEmpty(targetPath)) {
												path = DOWNLOAD_PATH + context.getPackageName();
											} else {
												path = targetPath;
											}
											if (TextUtils.isEmpty(fileName)) {
												if (!TextUtils.isEmpty(url)) {
													name = FileUtility.getMD5Str(url);
												} else {
													name = file.getName();
												}
											} else {
												name = fileName;
											}
											File mFile = new File(path, name);
											if (mFile.isFile()) {
												String mfileMD5 = FileUtility.getFileMD5(mFile);
												// 检测当前路径下是否已经存在需要下载的文件
												if (fileMD5.equals(mfileMD5)) {
													if (downloadListener != null) {
														DownloadError downloadError = new DownloadError(
																DownloadError.ErrorType.DOWNLOADFILE_EXISTING);
														downloadError.setUrl(url);
														downloadError.setFile(file);
														downloadListener.onFailure(downloadError);
													}
													return;
												}
											}
											File targetFile = copyFile(mFile, file);
											if (targetFile != null) {
												if (downloadListener != null) {
													downloadListener.onSuccess(url, null, mTypeInfos.get(fileMD5),
															targetFile);
												}
												return;
											}
										}
									}
								} catch (Exception e) {
									mLogger.printStackTrace(e);
									mLogger.errorLog("检测MD5值失败！");
									mMD5Infos.remove(md5);
									mTypeInfos.remove(md5);
								}
							}
							startDown(context, url, targetPath, fileName, gorup, forceDownload, downloadListener);
						}
					}
				}
			};
			if (mIsInitDone) {
				downloadThread.start();
			} else {
				if (mDownloadTreads == null)
					mDownloadTreads = new ArrayList<Thread>();
				mDownloadTreads.add(downloadThread);
			}
		} else {
			mLogger.errorLog("context 不能为null");
			if (downloadListener != null) {
				DownloadError downloadError = new DownloadError(DownloadError.ErrorType.CONTEXT_ERROR);
				downloadError.setUrl(url);
				downloadListener.onFailure(downloadError);
			}
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param context
	 * @param url
	 *            下载地址
	 * @param md5
	 *            文件MD5值
	 * @param group
	 *            下载任务所在组
	 * @param downloadListener
	 *            下载监听
	 */
	public void download(Context context, String url, String md5, String group, DownloadListener downloadListener) {
		download(context, url, null, null, false, md5, group, downloadListener);
	}

	/**
	 * 带group 下载文件，在暂停时，可以按组暂停任务
	 * 
	 * @param context
	 * @param url
	 *            下载地址
	 * @param group
	 *            下载任务所在组
	 * @param downloadListener
	 */
	public void downloadForGroup(Context context, String url, String group, DownloadListener downloadListener) {
		download(context, url, null, null, false, null, group, downloadListener);
	}

	/**
	 * 
	 * @param context
	 * @param url
	 *            下载地址
	 * @param md5
	 *            文件MD5值
	 * @param downloadListener
	 *            下载监听
	 */
	public void download(Context context, String url, String md5, DownloadListener downloadListener) {
		download(context, url, null, null, false, md5, null, downloadListener);
	}

	/**
	 * 复制文件
	 * 
	 * @param targetFile
	 * @param originFile
	 * @return File 复制完成的文件即targetFile，如果复制失败返回null
	 */
	private File copyFile(File targetFile, File originFile) {
		try {
			if (!targetFile.isFile()) {
				if (targetFile.getParentFile() != null && !targetFile.getParentFile().exists()) {
					targetFile.getParentFile().mkdirs();
				}
				targetFile.createNewFile();
			}
			return FileUtility.fileChannelCopy(originFile, targetFile);
		} catch (IOException e) {
			mLogger.printStackTrace(e);
			mLogger.errorLog("拷贝文件失败！");
			return null;
		}
	}

	/**
	 * 往Properties保存download信息
	 * 
	 */
	public static void saveProperties(Context mContext, String fileMD5Str, String absolutePath, String contentType) {
		try {
			String tagerPath = DOWNLOAD_PATH + mContext.getPackageName();
			File dirFile = new File(tagerPath);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			File md5File = new File(dirFile, PROTMD5_NAME);
			if (!md5File.isFile()) {
				md5File.createNewFile();
			}
			Properties md5Properties = new Properties();
			FileInputStream md5InputStream = new FileInputStream(md5File);
			md5Properties.load(md5InputStream);
			md5Properties.setProperty(fileMD5Str, absolutePath);
			FileOutputStream md5OutStream = new FileOutputStream(md5File);
			md5Properties.store(md5OutStream, PROPERTIES_SAVE_COMMENT);
			IOUtils.closeStream(md5InputStream);
			IOUtils.closeStream(md5OutStream);
			if (!TextUtils.isEmpty(contentType)) {
				File typeFile = new File(dirFile, PROTCONTENTTYPE_NAME);
				if (!typeFile.isFile()) {
					typeFile.createNewFile();
				}
				Properties typeProperties = new Properties();
				FileInputStream typeInputStream = new FileInputStream(typeFile);
				typeProperties.load(typeInputStream);
				typeProperties.setProperty(fileMD5Str, contentType);
				FileOutputStream typeOutStream = new FileOutputStream(typeFile);
				typeProperties.store(typeOutStream, PROPERTIES_SAVE_COMMENT);
				IOUtils.closeStream(typeOutStream);
				IOUtils.closeStream(typeInputStream);
			}
		} catch (Exception e) {
			mLogger.printStackTrace(e);
			mLogger.errorLog("存储下载信息失败！");
		}
	}

	/**
	 * 检测必要的参数，将信息传递给DownloadTheard，开始下载
	 */
	private void startDown(Context context, String url, String tagerPath, String fileName, String group,
			boolean forceDownload, DownloadListener downloadListener) {
		if (null != mThreadPool && !mThreadPool.isShutdown()) {
			if (!TextUtils.isEmpty(group)) {
				if (mGroups == null) {
					mGroups = new HashMap<String, List<String>>();
				}
				List<String> groupUrls = mGroups.get(group);
				if (groupUrls == null) {
					groupUrls = new ArrayList<String>();
				}
				groupUrls.add(url);
				mGroups.put(group, groupUrls);
			}
			DownloadTheard theard = new DownloadTheard(context, url, tagerPath, fileName, forceDownload, group,
					downloadListener);
			addLoadUrl(url, theard);
			mThreadPool.submit(theard);
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param context
	 * @param url
	 * @param downloadListener
	 *            下载监听
	 */
	public void download(Context context, String url, DownloadListener downloadListener) {
		download(context, url, null, null, false, null, null, downloadListener);
	}
}
