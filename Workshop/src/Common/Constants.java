package Common;

import java.io.File;

import android.os.Environment;

public class Constants {

	public static final String ROOT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
	public static final String  PHOTO_DIR = ROOT + File.separator + "Camera" + File.separator;
	public static final String APP_PHOTO_DIR =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "SummaPhoto" + File.separator;
	public static final String APP_TEMP_DIR = new File(Environment.getExternalStorageDirectory(), "Summaphoto") + File.separator + "Temp" + File.separator;
	
}
