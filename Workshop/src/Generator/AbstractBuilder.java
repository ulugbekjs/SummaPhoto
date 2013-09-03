package Generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.example.aworkshop.SettingsActivity;

import ActivationManager.DedicatedRequest;
import Common.ActualEvent;
import Common.ActualEventsBundle;
import Common.Photo;
import Common.PhotoContainer;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.provider.Contacts.Photos;

/**
 * This is an abstract class for Builder objects which contain logic of choosing a template, populating it with photos and drawing them onto the template
 * @author yonatan
 *
 */
public abstract class AbstractBuilder {

	ActualEventsBundle bundle;
	AbstractTemplate template;

	public AbstractBuilder(ActualEventsBundle bundle) {
		this.bundle = bundle;
	}
	// methods to override
	
	public abstract Photo buildCollage();
	public abstract boolean populateTemplate();

	/**
	 * Chooses best template for bundle, sets this.template if one is found
	 * @return request if non matching template is found
	 */
	public DedicatedRequest setTemplate() {
		int[] templateDiffs = new int[BlockTemplate.BLOCK_TEMPLATES_NUM];
		int[] templateRemaining = new int[BlockTemplate.BLOCK_TEMPLATES_NUM];

		BlockTemplate[] templates = new BlockTemplate[BlockTemplate.BLOCK_TEMPLATES_NUM];
		for (int i=0; i<BlockTemplate.BLOCK_TEMPLATES_NUM; i++) {
			templates[i] = BlockTemplate.getTemplate(i+1);
		}

		// calculate difference of required vertical and horizontal photos for each template
		for (int t=0; t<BlockTemplate.BLOCK_TEMPLATES_NUM; t++) {
			int diffHorizontal = Math.max(0, templates[t].horizontalSlots.size() - bundle.horizontalCount());
			int diffVertical = Math.max(0, templates[t].verticalSlots.size() - bundle.verticalCount());
			templateDiffs[t] = Math.abs(diffHorizontal - diffVertical);
			templateRemaining[t] = diffHorizontal + diffVertical;
		}
		
		// calculate compatibility scores
		double[] templateScores = new double[BlockTemplate.BLOCK_TEMPLATES_NUM];
		for (int i = 0; i<BlockTemplate.BLOCK_TEMPLATES_NUM; i++) {
			templateScores[i] = templateDiffs[i] * 0.3 + templateRemaining[i] * 0.7;
		}

		BlockTemplate chosenTemplate = null;
		double min = Double.MAX_VALUE;
		int minIndex = -1;

		// get a fitting template or pick the "closest" one
		for (int i = 0; i<BlockTemplate.BLOCK_TEMPLATES_NUM; i++) {
			if (templates[i].getHorizontalSlots().size() <= bundle.horizontalCount() &&
					templates[i].getVerticalSlots().size() <= bundle.verticalCount()) { // template fits perfectly for bundle
				chosenTemplate = templates[i];
				break;
			}
			if (templateScores[i] < min) { 
				min = templateScores[i];
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
				request.setHorizontalNeeded(Math.max(0, templates[minIndex].horizontalSlots.size() - bundle.horizontalCount()));
				request.setVerticalNeeded(Math.max(0, templates[minIndex].verticalSlots.size() - bundle.verticalCount()));
			}
		}

		return request;
	}
	
	/**
	 * Gets random photos from event with respect to the template. Must be run after setTemplate!
	 * @param SlotsToFill
	 * @param pickedPhotos
	 * @return true if pickedPhotos.size() >= slotsToFill.size()
	 */
	protected boolean getHorizontalPhotosForTemplate(List<Photo> pickedPhotos) {
		if (template == null)
			return false;

		List<Integer> horizontalSlotsToFill = template.getHorizontalSlots();
		// placing horizontal photos
		return getPhotosWithRespectToTemplateSlots(horizontalSlotsToFill, true, pickedPhotos);	
	}
	
	protected boolean getVerticalPhotosForTemplate(List<Photo> pickedPhotos) {
		if (template == null)
			return false;
		List<Integer> verticalSlotsToFill = template.getVerticalSlots();
		// placing horizontal photos
		return getPhotosWithRespectToTemplateSlots(verticalSlotsToFill, false, pickedPhotos);	
	}

	private Queue<ActualEvent> getQueue(boolean shuffle) {
		Queue<ActualEvent> queue = new LinkedList<ActualEvent>();
		List<ActualEvent> events = bundle.getActualEvents();
		if (shuffle) {
			Collections.shuffle(events);
		}
		
		Collections.reverse(events); // so we start from the other side of the events, that we might have not reached

		for (ActualEvent event: events) {
			queue.add(event);
		}
		return queue;
	}
	
	private boolean getPhotosWithRespectToTemplateSlots(List<Integer> slotsToFill, boolean horizontalPhotos, List<Photo> pickedPhotos) {

		if (pickedPhotos == null) {
			pickedPhotos = new LinkedList<Photo>();
		}
		
		Queue<ActualEvent> queue = getQueue(horizontalPhotos);

		while (!slotsToFill.isEmpty() && !queue.isEmpty()) {
			ActualEvent event = queue.remove();

			List<Photo> photosInEvent = (horizontalPhotos) ? event.horizontalPhotos() : event.verticalPhotos(); 

			if (!photosInEvent.isEmpty()) { // horizontal photos in event
				Random random = new Random(new Date().getTime());
				int rand = random.nextInt(photosInEvent.size());
				pickedPhotos.add(photosInEvent.remove(rand));
				slotsToFill.remove(0);
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


	protected void addSlotImageToCanvasBySampling(Bitmap bitmap, Canvas canvas, Slot slot, int sampleSize) {

		// get Image bitmap
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inSampleSize = sampleSize;
		options.inDither = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[32 * 1024];

		bitmap = BitmapFactory.decodeFile(slot.getPhoto().getFilePath(), options);
		bitmap.setHasAlpha(true);
		WeakReference<Bitmap> bitmapReference = new WeakReference<Bitmap>(bitmap);

		//		Bitmap bitmap = decodeScaledBitmapFromSdCard(slot.getPhoto().getFilePath(), slot.getPhoto().getWidth(), slot.getPhoto().getWidth());

		// resize image
		if (bitmapReference.get() != null) {
			bitmap = bitmapReference.get();
		}
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

		bitmap.recycle();
		bitmap = null;

	}
	protected Photo saveCollage(Bitmap bmpBase) throws IOException {
		Calendar calendar = Calendar.getInstance();
		File file = null;
		FileOutputStream fos = null;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
		file = new File
				(SettingsActivity.APP_PHOTO_DIR, "summaphoto_" + formatter.format(calendar.getTime()) + ".jpg");

		// Save Bitmap to File
		fos = new FileOutputStream(file);
		bmpBase.compress(Bitmap.CompressFormat.JPEG, 100, fos);

		fos.flush();
		fos.close();
		fos = null;

		bmpBase.recycle();
		bmpBase = null;

		return new Photo(calendar.getTime(), 3264, 2488, null, file.getAbsolutePath());
	}

	protected void clearProcessedPhotos() {
		PhotoContainer.getInstance().clearProcessPhotos();
	}
	
	protected void drawFrame(Canvas canvas, int width, int height) {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(20f);        
		paint.setStyle(Paint.Style.STROKE);  
		canvas.drawRect(0, 0,width, height, paint);
	}



}
