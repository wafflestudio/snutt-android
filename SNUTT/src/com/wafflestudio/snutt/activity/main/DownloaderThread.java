package com.wafflestudio.snutt.activity.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.wafflestudio.snutt.R;

/**
 * Downloads a file in a thread. Will send messages to the
 * MainActivity activity to update the progress bar.
 */
public class DownloaderThread extends Thread
{
	public static final int MESSAGE_DOWNLOAD_STARTED = 1000;
	public static final int MESSAGE_DOWNLOAD_COMPLETE = 1001;
	public static final int MESSAGE_UPDATE_PROGRESS_BAR = 1002;
	public static final int MESSAGE_DOWNLOAD_CANCELED = 1003;
	public static final int MESSAGE_CONNECTING_STARTED = 1004;
	public static final int MESSAGE_ENCOUNTERED_ERROR = 1005;

	// constants
	private static final int DOWNLOAD_BUFFER_SIZE = 4096;

	// instance variables
	private MainActivity parentActivity;
	private String downloadUrl;
	private ProgressDialog progressDialog = null;

	/**
	 * Instantiates a new DownloaderThread object.
	 * @param parentActivity Reference to MainActivity activity.
	 * @param inUrl String representing the URL of the file to be downloaded.
	 */
	public DownloaderThread(MainActivity inParentActivity, int year, String semester)
	{
		downloadUrl = "http://snutt.kr/data/txt/" + year + "_" + semester + ".txt";
		parentActivity = inParentActivity;
	}

	/**
	 * Connects to the URL of the file, begins the download, and notifies the
	 * MainActivity activity of changes in state. Writes the file to
	 * the root of the SD card.
	 */
	@Override
	public void run()
	{
		URL url;
		URLConnection conn;
		int fileSize, lastSlash;
		String fileName;
		BufferedInputStream inStream;
		BufferedOutputStream outStream;
		File outFile;
		FileOutputStream fileStream;
		Message msg;

		// we're going to connect now
		msg = Message.obtain(activityHandler, MESSAGE_CONNECTING_STARTED, 0, 0, downloadUrl);
		activityHandler.sendMessage(msg);

		try
		{
			url = new URL(downloadUrl);
			conn = url.openConnection();
			conn.setUseCaches(false);
			fileSize = conn.getContentLength();

			// get the filename
			lastSlash = url.toString().lastIndexOf('/');
			fileName = "file.bin";
			if(lastSlash >=0)
			{
				String[] tmp = url.toString().substring(lastSlash + 1).split("\\.");
				fileName = tmp[0];
			}
			if(fileName.equals(""))
			{
				fileName = "file.bin";
			}

			// notify download start
			int fileSizeInKB = fileSize / 1024;
			msg = Message.obtain(activityHandler, MESSAGE_DOWNLOAD_STARTED, fileSizeInKB, 0, fileName);
			activityHandler.sendMessage(msg);

			// start download
			inStream = new BufferedInputStream(conn.getInputStream());
			//outFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
			outFile = new File(parentActivity.getFilesDir() + "/" + fileName + ".tmp");
			fileStream = new FileOutputStream(outFile);
			outStream = new BufferedOutputStream(fileStream, DOWNLOAD_BUFFER_SIZE);
			byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
			int bytesRead = 0, totalRead = 0;
			while(!isInterrupted() && (bytesRead = inStream.read(data, 0, data.length)) >= 0)
			{
				outStream.write(data, 0, bytesRead);

				// update progress bar
				totalRead += bytesRead;
				int totalReadInKB = totalRead / 1024;
				msg = Message.obtain(activityHandler, MESSAGE_UPDATE_PROGRESS_BAR, totalReadInKB, 0);
				activityHandler.sendMessage(msg);
			}

			outStream.close();
			fileStream.close();
			inStream.close();
			
			if(isInterrupted())
			{
				// the download was canceled, so let's delete the partially downloaded file
				outFile.delete();
			}
			else
			{
				// notify completion
				msg = Message.obtain(activityHandler, MESSAGE_DOWNLOAD_COMPLETE);
				activityHandler.sendMessage(msg);
				
				//다운로드가 완료되면 파일명 변경! .tmp 제거
				File fromFile = new File(parentActivity.getFilesDir() + "/" + fileName + ".tmp");
				File targetFile = new File(parentActivity.getFilesDir() + "/" + fileName + ".txt");
				if (fromFile.exists()){
					fromFile.renameTo(targetFile);
					System.out.println("파일명 변경 ! : " + fromFile.toString() + " to " + targetFile.toString());
				}

			}
		}
		catch(MalformedURLException e)
		{
			String errMsg = parentActivity.getString(R.string.error_message_bad_url);
			msg = Message.obtain(activityHandler,
					MESSAGE_ENCOUNTERED_ERROR,
					0, 0, errMsg);
			activityHandler.sendMessage(msg);
		}
		catch(FileNotFoundException e)
		{
			String errMsg = parentActivity.getString(R.string.error_message_file_not_found);
			msg = Message.obtain(activityHandler,
					MESSAGE_ENCOUNTERED_ERROR,
					0, 0, errMsg);
			activityHandler.sendMessage(msg); 
		}
		catch(Exception e)
		{
			String errMsg = parentActivity.getString(R.string.error_message_general);
			msg = Message.obtain(activityHandler,
					MESSAGE_ENCOUNTERED_ERROR,
					0, 0, errMsg);
			activityHandler.sendMessage(msg); 
		}
	}
	
	private Handler activityHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			/*
			 * Handling MESSAGE_UPDATE_PROGRESS_BAR:
			 * 1. Get the current progress, as indicated in the arg1 field
			 *    of the Message.
			 * 2. Update the progress bar.
			 */
			case MESSAGE_UPDATE_PROGRESS_BAR:
				if(progressDialog != null)
				{
					int currentProgress = msg.arg1;
					progressDialog.setProgress(currentProgress);
				}
				break;

				/*
				 * Handling MESSAGE_CONNECTING_STARTED:
				 * 1. Get the URL of the file being downloaded. This is stored
				 *    in the obj field of the Message.
				 * 2. Create an indeterminate progress bar.
				 * 3. Set the message that should be sent if user cancels.
				 * 4. Show the progress bar.
				 */
			case MESSAGE_CONNECTING_STARTED:
				if(msg.obj != null && msg.obj instanceof String)
				{
					String url = (String) msg.obj;
					// truncate the url
					if(url.length() > 16)
					{
						String tUrl = url.substring(0, 15);
						tUrl += "...";
						url = tUrl;
					}
					String pdTitle = parentActivity.getString(R.string.progress_dialog_title_connecting);
					String pdMsg = parentActivity.getString(R.string.progress_dialog_message_prefix_connecting);
					pdMsg += " " + url;

					dismissCurrentProgressDialog();
					progressDialog = new ProgressDialog(parentActivity);
					progressDialog.setTitle(pdTitle);
					progressDialog.setMessage(pdMsg);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setIndeterminate(true);
					// set the message to be sent when this dialog is canceled
					Message newMsg = Message.obtain(this, MESSAGE_DOWNLOAD_CANCELED);
					progressDialog.setCancelMessage(newMsg);
					progressDialog.show();
				}
				break;

				/*
				 * Handling MESSAGE_DOWNLOAD_STARTED:
				 * 1. Create a progress bar with specified max value and current
				 *    value 0; assign it to progressDialog. The arg1 field will
				 *    contain the max value.
				 * 2. Set the title and text for the progress bar. The obj
				 *    field of the Message will contain a String that
				 *    represents the name of the file being downloaded.
				 * 3. Set the message that should be sent if dialog is canceled.
				 * 4. Make the progress bar visible.
				 */
			case MESSAGE_DOWNLOAD_STARTED:
				// obj will contain a String representing the file name
				if(msg.obj != null && msg.obj instanceof String)
				{
					int maxValue = msg.arg1;
					String fileName = (String) msg.obj;
					String pdTitle = parentActivity.getString(R.string.progress_dialog_title_downloading);
					String pdMsg = parentActivity.getString(R.string.progress_dialog_message_prefix_downloading);
					pdMsg += " " + fileName;

					dismissCurrentProgressDialog();
					progressDialog = new ProgressDialog(parentActivity);
					progressDialog.setTitle(pdTitle);
					progressDialog.setMessage(pdMsg);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					progressDialog.setProgress(0);
					progressDialog.setMax(maxValue);
					// set the message to be sent when this dialog is canceled
					Message newMsg = Message.obtain(this, MESSAGE_DOWNLOAD_CANCELED);
					progressDialog.setCancelMessage(newMsg);
					progressDialog.setCancelable(true);
					progressDialog.show();
				}
				break;

				/*
				 * Handling MESSAGE_DOWNLOAD_COMPLETE:
				 * 1. Remove the progress bar from the screen.
				 * 2. Display Toast that says download is complete.
				 */
			case MESSAGE_DOWNLOAD_COMPLETE:
				dismissCurrentProgressDialog();
				displayMessage(parentActivity.getString(R.string.user_message_download_complete));
				break;

				/*
				 * Handling MESSAGE_DOWNLOAD_CANCELLED:
				 * 1. Interrupt the downloader thread.
				 * 2. Remove the progress bar from the screen.
				 * 3. Display Toast that says download is complete.
				 */
			case MESSAGE_DOWNLOAD_CANCELED:
				dismissCurrentProgressDialog();
				displayMessage(parentActivity.getString(R.string.user_message_download_canceled));
				break;

				/*
				 * Handling MESSAGE_ENCOUNTERED_ERROR:
				 * 1. Check the obj field of the message for the actual error
				 *    message that will be displayed to the user.
				 * 2. Remove any progress bars from the screen.
				 * 3. Display a Toast with the error message.
				 */
			case MESSAGE_ENCOUNTERED_ERROR:
				// obj will contain a string representing the error message
				if(msg.obj != null && msg.obj instanceof String)
				{
					String errorMessage = (String) msg.obj;
					dismissCurrentProgressDialog();
					displayMessage(errorMessage);
				}
				break;

			default:
				// nothing to do here
				break;
			}
		}
	};
	/**
	 * If there is a progress dialog, dismiss it and set progressDialog to
	 * null.
	 */
	public void dismissCurrentProgressDialog()
	{
		if(progressDialog != null)
		{
			progressDialog.hide();
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * Displays a message to the user, in the form of a Toast.
	 * @param message Message to be displayed.
	 */
	public void displayMessage(String message)
	{
		if(message != null)
		{
			Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show();
		}
	}

}
