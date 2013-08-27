package Common;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class PhotoFilter {

	public static void filter() {

		final File ROOT = new File(Environment.getExternalStorageDirectory(), "DCIM");
		final String  PHOTO_DIR = ROOT + File.separator + "Tests" + File.separator + "IMG_20130804_130626.jpg";
		
		Bitmap bmp = decodeScaledBitmapFromSdCard(PHOTO_DIR, 2448, 3264);

		bmp = bmp.copy(Bitmap.Config.ARGB_8888, false);  	

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		//Initialize the intArray with the same size as the number of pixels on the image  
		int[]  intArray = new int[bmp.getWidth()*bmp.getHeight()];  
		
		//copy pixel data from the Bitmap into the 'intArray' array  
		bmp.getPixels(intArray, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());  
		bmp.recycle();
		
		Sobel sobel = new Sobel();
		sobel.init(intArray, width, height);
		int[] res = sobel.process();

	}
	
	private static Bitmap decodeScaledBitmapFromSdCard(String filePath,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(filePath, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(filePath, options);
	}

	private static int calculateInSampleSize(
	        BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);

	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }

	    return inSampleSize;
	}
	
	
}
