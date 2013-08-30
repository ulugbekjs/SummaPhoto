package Generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import Common.ActualEventsBundle;
import Common.Photo;
import Common.PhotoContainer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

/**
 * This is an abstract class for Builder objects which contain logic of choosing a template, populating it with photos and drawing them onto the template
 * @author yonatan
 *
 */
public abstract class AbstractBuilder {

	ActualEventsBundle bundle;
	
	public AbstractBuilder(ActualEventsBundle bundle) {
		this.bundle = bundle;
	}
	
	
	public abstract boolean populateTemplate();
	public abstract Photo buildCollage();

	protected Bitmap decodeScaledBitmapFromSdCard(String filePath,
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

	private int calculateInSampleSize(
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

	protected void addSlotImageToCanvas(Canvas canvas, Slot slot) {

		// get Image bitmap
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		//		Bitmap bitmap = BitmapFactory.decodeFile(slot.getPhoto().getFilePath());
		Bitmap bitmap = decodeScaledBitmapFromSdCard(slot.getPhoto().getFilePath(), slot.getPhoto().getWidth(), slot.getPhoto().getWidth());

		// resize image
		int[] dimensions = slot.getProportionateDimensionsForSlot(bitmap.getWidth(), bitmap.getHeight());
		bitmap = Bitmap.createScaledBitmap(bitmap, dimensions[0], dimensions[1], true);

		// crop image
		bitmap = Bitmap.createBitmap(bitmap, 0,0, (int)slot.getSlotWidth(), (int) slot.getSlotHeight());

		// draw bitmap onto canvas
		PixelPoint topleftPixelPoint = slot.getTopLeft();
		PixelPoint bottomRightPixelPoint = slot.getBottomRight();

		canvas.drawBitmap(bitmap,
				new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), // take all source photo
				new Rect(topleftPixelPoint.getX(), // place in output photo
						topleftPixelPoint.getY(),
						bottomRightPixelPoint.getX(), 
						bottomRightPixelPoint.getY()), 
						null);

		//free bitmap
		bitmap.recycle();
		bitmap = null;
	}

	protected Photo saveCollage(Bitmap bmpBase) throws IOException {
		Calendar calendar = Calendar.getInstance();
		File externalStorageDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
		File testsDir = new File(externalStorageDir.getAbsolutePath() + File.separator + "Output");
		File file = null;
		FileOutputStream fos = null;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
		file = new File
				(testsDir, "summaphoto_" + formatter.format(calendar.getTime()) + ".jpg");

		// Save Bitmap to File
		fos = new FileOutputStream(file);
		bmpBase.compress(Bitmap.CompressFormat.JPEG, 70, fos);

		fos.flush();
		fos.close();
		fos = null;

		bmpBase.recycle();
		bmpBase = null;

		return new Photo(calendar.getTime(), 3264, 2488, null, file.getAbsolutePath());
	}
	
	protected void clearProcessPhotos() {
		PhotoContainer.getInstance().clearProcessPhotos();
	}

}
