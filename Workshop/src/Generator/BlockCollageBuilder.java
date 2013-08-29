package Generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import javax.xml.transform.Templates;

import org.joda.time.DateTime;
import org.junit.experimental.max.MaxCore;

import ActivationManager.DedicatedRequest;
import Common.ActualEvent;
import Common.ActualEventsBundle;
import Common.Photo;
import Partitioning.Cluster;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

public class BlockCollageBuilder {

	private static final String TAG = "Generator.BlockCollageBuilder";
	ActualEventsBundle bundle = null;
	BlockTemplate template = null;

	public BlockCollageBuilder(ActualEventsBundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * Get a template, or fill a request with needed parameters
	 * @param request - this will be filled if no template is fitting
	 * @param bundle - this bundle with the events
	 * @return chosen Template
	 */
	public DedicatedRequest setTemplate() {
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

		DedicatedRequest request = null;

		if (chosenTemplate != null) { // template was chosen
			this.template = chosenTemplate;
		}
		else { // need to fill DedicatedRequest
			if (minIndex != -1)  { // should be true
				request = new DedicatedRequest();
				request.setHorizontalNeeded(templates[minIndex].horizontalSlots.size() - bundle.horizontalCount());
				request.setVerticalNeeded(templates[minIndex].verticalSlots.size() - bundle.verticalCount());
			}
		}
		
		return request;
	}

	public boolean populateTemplate() {
		
		boolean successful = true;
		
		List<Integer> horizontals = new LinkedList<Integer>(template.getHorizontalSlots());
		List<Integer> verticals = new LinkedList<Integer>(template.getVerticalSlots());
		
		// placing horizontal photos
		successful = populateSubSlots(horizontals, true);
		
		// placing vertical photos
		successful = populateSubSlots(verticals, false);
		
		return successful;
	}

	private boolean populateSubSlots(List<Integer> slotsToFill, boolean horizontalPhotos) {
		
		Queue<ActualEvent> queue = getQueue();

		while (!slotsToFill.isEmpty() && !queue.isEmpty()) {
			ActualEvent event = queue.remove();
			
			List<Photo> photosInEvent = (horizontalPhotos) ? event.horizontalPhotos() : event.verticalPhotos(); 
			
			if (!photosInEvent.isEmpty()) { // horizontal photos in event
				Random random = new Random(new Date().getTime());
				int rand = random.nextInt(event.getEventSize()); 
				template.getSlot(slotsToFill.remove(0)).assignToPhoto(photosInEvent.remove(rand));
			}
			
			if (!photosInEvent.isEmpty()) { // still horizontal photos left in event 
				queue.add(event); // return to queue
			}
		}
		
		if (!slotsToFill.isEmpty()) { // events ran out before full population
			return false;
		}
		else {
			return true;
		}
	}
		
		
		
		
//		// placing vertical photos in slots
//		Queue<ComparableActualEvent> queue = getComparableEventPriorityQueue();


//		old brute force algorithm
//		List<Integer> horizontals = new LinkedList<Integer>(template.getHorizontalSlots());
//		List<Integer> verticals = new LinkedList<Integer>(template.getVerticalSlots());
//		List<Photo> horizontalPhotos;
//		List<Photo> verticalPhotos;
//
//		while (!horizontals.isEmpty()) {
//			Random rand = new Random(new Date().getTime());
//			int  n = rand.nextInt(50) + 1;
//
//
//			for (ActualEvent event: bundle.getActualEvents()) {
//				horizontalPhotos = event.horizontalPhotos();
//
//				if (!horizontalPhotos.isEmpty()) {
//					template.getSlot(horizontals.remove(0)).assignToPhoto(horizontalPhotos.remove(0));
//					break;
//				}
//			}
//		}
//
//		while (!verticals.isEmpty()) {
//
//			for (ActualEvent event: bundle.getActualEvents()) {
//				verticalPhotos = event.verticalPhotos();
//
//				if (!verticalPhotos.isEmpty()) {
//					template.getSlot(verticals.remove(0)).assignToPhoto(verticalPhotos.remove(0));
//					break;
//				}
//			}
//		}
//		return false;
//	}

	private Queue<ActualEvent> getQueue() {
//		Queue<ComparableActualEvent> queue = new PriorityQueue<ComparableActualEvent>(bundle.getActualEvents().size(), new Comparator<ComparableActualEvent>() {
//
//			@Override
//			public int compare(ComparableActualEvent lhs,
//					ComparableActualEvent rhs) {
//				return Integer.valueOf(lhs.photosRemoved).compareTo(rhs.photosRemoved);
//			}
//		});
		
		Queue<ActualEvent> queue = new LinkedList<ActualEvent>();
		for (ActualEvent event: bundle.getActualEvents()) {
			queue.add(event);
		}
		return queue;
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
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("summaphoto_yyyy_MM_dd_HH_mm");
			file = new File
					(testsDir, formatter.format(calendar.getTime()) + ".jpg");

			fos = new FileOutputStream(file);
			bmpBase.compress(Bitmap.CompressFormat.JPEG, 70, fos);

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

	private Bitmap decodeScaledBitmapFromSdCard(String filePath,
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
 
}
