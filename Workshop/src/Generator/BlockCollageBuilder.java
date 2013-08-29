package Generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.Templates;

import org.junit.experimental.max.MaxCore;

import ActivationManager.DedicatedRequest;
import Common.ActualEvent;
import Common.ActualEventsBundle;
import Common.Photo;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

public class BlockCollageBuilder {
	
	private static final String TAG = "Generator.BlockCollageBuilder";

	public BlockCollageBuilder(ActualEventsBundle bundle) {
	}

	/**
	 * Get a template, or fill a request with needed parameters
	 * @param request - this will be filled if no template is fitting
	 * @param bundle - this bundle with the events
	 * @return chosen Template
	 */
	public static BlockTemplate chooseTemplate(ActualEventsBundle bundle, DedicatedRequest request) {
		int[] templateDiffs = new int[BlockTemplate.BLOCK_TEMPLATES_NUM];

		BlockTemplate[] templates = new BlockTemplate[BlockTemplate.BLOCK_TEMPLATES_NUM];
		for (int i=0; i<BlockTemplate.BLOCK_TEMPLATES_NUM; i++) {
			templates[i] = BlockTemplate.getTemplate(i+1);
		}

		// calculate difference of required vertical and horizontal photos for each template
		for (int t=0; t<BlockTemplate.BLOCK_TEMPLATES_NUM; t++) {
			int diffHorizontal = Math.max(0, templates[t].horizontalSlots.size() - bundle.horizontalCount());
			int diffVertical = Math.max(0, templates[t].verticalSlots.size() - bundle.verticalCount());
			templateDiffs[t] = Math.abs(diffHorizontal - diffVertical);
		}

		BlockTemplate chosenTemplate = null;
		int min = Integer.MAX_VALUE, minIndex = -1;

		for (int i = 0; i<BlockTemplate.BLOCK_TEMPLATES_NUM; i++) {
			if (templateDiffs[i] == 0) { // template fits perfectly for bundle
				chosenTemplate = templates[i];
				break;
			}
			if (templateDiffs[i] < min) {  // template has better (lower) diff
				min = templateDiffs[i];
				minIndex = i;
			}
		}

		if (chosenTemplate != null) { // template was chosen
			return chosenTemplate;
		}
		else { // need to fill DedicatedRequest
			if (minIndex != -1)  { // should be true
				request = new DedicatedRequest();
				request.setHorizontalNeeded(templates[minIndex].horizontalSlots.size() - bundle.horizontalCount());
				request.setVerticalNeeded(templates[minIndex].verticalSlots.size() - bundle.verticalCount());
			}
			return null;
		}
	}

	public static boolean populateTemplate(ActualEventsBundle bundle, BlockTemplate template) {

		List<Integer> horizontals = new LinkedList<Integer>(template.getHorizontalSlots());
		List<Integer> verticals = new LinkedList<Integer>(template.getVerticalSlots());
		List<Photo> horizontalPhotos;
		List<Photo> verticalPhotos;

		while (!horizontals.isEmpty()) {

			for (ActualEvent event: bundle.getActualEvents()) {
				horizontalPhotos = event.horizontalPhotos();

				if (!horizontalPhotos.isEmpty()) {
					template.getSlot(horizontals.remove(0)).assignToPhoto(horizontalPhotos.remove(0));
					break;
				}
			}
		}

		while (!verticals.isEmpty()) {

			for (ActualEvent event: bundle.getActualEvents()) {
				verticalPhotos = event.verticalPhotos();

				if (!verticalPhotos.isEmpty()) {
					template.getSlot(verticals.remove(0)).assignToPhoto(verticalPhotos.remove(0));
					break;
				}
			}
		}
		return false;
	}

	public static File BuildCollage(BlockTemplate template) {

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
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			file = new File
					(testsDir, formatter.format(calendar.getTime()) + ".jpg");

			fos = new FileOutputStream(file);
			bmpBase.compress(Bitmap.CompressFormat.JPEG, 60, fos);

			fos.flush();
			fos.close();
			fos = null;

			bmpBase.recycle();
			bmpBase = null;
		}
		catch (IOException e) {
			Log.e(TAG, "Error when saving collage file +" + file.getPath());
			// TODO: notify user about error in saving collage
			file = null;
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

	private static void addSlotImageToCanvas(Canvas canvas, Slot slot) {

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
