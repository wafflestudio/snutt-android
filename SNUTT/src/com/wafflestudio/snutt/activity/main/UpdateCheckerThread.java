package com.wafflestudio.snutt.activity.main;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wafflestudio.snutt.data.Lecture;

/**
 * Downloads a file in a thread. Will send messages to the
 * MainActivity activity to update the progress bar.
 */
public class UpdateCheckerThread extends Thread
{
	private static final int DOWNLOAD_BUFFER_SIZE = 256;
	int year;
	String semester;
	private String downloadUrl;
	MainActivity parentActivity;

	/**
	 * Instantiates a new DownloaderThread object.
	 * @param parentActivity Reference to MainActivity activity.
	 * @param inUrl String representing the URL of the file to be downloaded.
	 */
	public UpdateCheckerThread(MainActivity parentActivity, int year, String semester)
	{
		this.parentActivity = parentActivity;
		this.year = year;
		this.semester = semester;
		this.downloadUrl = "http://snutt.kr/data/txt/" + year + "_" + semester + ".txt";
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
		BufferedInputStream inStream;
		try
		{
			url = new URL(downloadUrl);
			conn = url.openConnection();
			conn.setUseCaches(false);

			// start download
			inStream = new BufferedInputStream(conn.getInputStream());
			//outFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
			byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
			int bytesRead = inStream.read(data, 0, data.length);
			inStream.close();

			String header = new String(data, 0, bytesRead);

			if (!isInterrupted()){
				String[] lines = header.split("\n");
				if (lines.length > 1){
					String updatedDate = lines[1].trim(); 
					Pattern pattern = Pattern.compile("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]");
					Matcher matcher = pattern.matcher(updatedDate);
					if (matcher.find() && !updatedDate.equals(Lecture.updatedDate.trim())){
						System.out.println(updatedDate + " / " + Lecture.updatedDate.trim());
						//업데이트 해야할 때
						parentActivity.confirmSugangUpdate(year, semester);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("에러");
		}
	}

}
