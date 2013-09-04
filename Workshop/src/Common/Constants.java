package Common;

import java.io.File;

import android.os.Environment;

public class Constants {

	public static final String ROOT = new File(Environment.getExternalStorageDirectory(), "DCIM").getAbsolutePath();
	public static final String  PHOTO_DIR = ROOT + File.separator + "Camera" + File.separator;
	public static final String APP_PHOTO_DIR =  new File(Environment.getExternalStorageDirectory(), "Pictures") + File.separator + "SummaPhoto" + File.separator;
	public static final String APP_TEMP_DIR = new File(Environment.getExternalStorageDirectory(), "Summaphoto") + File.separator + "Temp" + File.separator;

}
