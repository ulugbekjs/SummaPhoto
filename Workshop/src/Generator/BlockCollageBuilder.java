package Generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.im4java.core.UFRawCmd;

import com.example.aworkshop.R;
import com.example.aworkshop.SettingsActivity;

import ActivationManager.EventCandidate;
import Common.ActualEvent;
import Common.Photo;
import android.R.integer;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

public class BlockCollageBuilder {

	BlockTemplate template;
	List<EventCandidate> events;

	public BlockCollageBuilder(BlockTemplate template, List<EventCandidate> events) {
		this.template = template;
		this.events = events;
	}

	public boolean populateTemplate() {

		List<Integer> horizontals = new LinkedList<Integer>(template.getHorizontalSlots());
		List<Integer> verticals = new LinkedList<Integer>(template.getVerticalSlots());
		List<Photo> horizontalPhotos;
		List<Photo> verticalPhotos;

		while (!horizontals.isEmpty()) {

			for (EventCandidate event: events) {
				horizontalPhotos = event.horizontalPhotos();

				if (!horizontalPhotos.isEmpty()) {
					template.getSlot(horizontals.remove(0)).assignToPhoto(horizontalPhotos.remove(0));
					break;
				}
			}
		}

		while (!verticals.isEmpty()) {

			for (EventCandidate event: events) {
				verticalPhotos = event.verticalPhotos();

				if (!verticalPhotos.isEmpty()) {
					template.getSlot(verticals.remove(0)).assignToPhoto(verticalPhotos.remove(0));
					break;
				}
			}
		}
		return false;
	}

	public File BuildCollage() {

		Canvas canvas = null;
		FileOutputStream fos = null;
		Bitmap bmpBase = null;

		bmpBase = Bitmap.createBitmap(3264, 2448, Bitmap.Config.RGB_565);
		canvas = new Canvas(bmpBase);

		// draw images saved in Template onto canvas
		for (int slot = 0; slot < template.getNumberOfSlots(); slot ++) {
			try {
				addSlotImageToCanvas(canvas, template.getSlot(slot));
			}
			catch (NullPointerException exception) {
				// TODO: deal with error
			}
		}

		File externalStorageDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
		File testsDir = new File(externalStorageDir.getAbsolutePath() + File.separator + "Output");
		File file = null;

		// Save Bitmap to File
		try	{
			file = new File(testsDir, "output.jpg");

			fos = new FileOutputStream(file);
			bmpBase.compress(Bitmap.CompressFormat.JPEG, 50, fos);

			fos.flush();
			fos.close();
			fos = null;
			
			bmpBase.recycle();
			bmpBase = null;
		}
		catch (IOException e) {
			// TODO: deal with error
			e.printStackTrace();
		}
		//		finally {
		//			if (fos != null) {
		//				try {
		//					fos.close();
		//					fos = null;
		//				}
		//				catch (Exception e) {
		//					// TODO: deal with error
		//					int x= 5;
		//					String xString = e.getMessage();
		//					e.printStackTrace();
		//					
		//				}
		//			}
		//
		//		}

		return file;
	}

	private void addSlotImageToCanvas(Canvas canvas, Slot slot) {

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
