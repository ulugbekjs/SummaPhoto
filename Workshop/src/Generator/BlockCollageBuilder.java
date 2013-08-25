package Generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.im4java.core.UFRawCmd;

import ActivationManager.EventCandidate;
import Common.ActualEvent;
import Common.Photo;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;

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
			bmpBase.compress(Bitmap.CompressFormat.JPEG, 100, fos);

			fos.flush();
			fos.close();
			fos = null;
		}
		catch (IOException e) {
			// TODO: deal with error
			e.printStackTrace();
		}
		finally {
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				}
				catch (Exception e) {
					// TODO: deal with error
					int x= 5;
					String xString = e.getMessage();
					e.printStackTrace();
					
				}
			}

		}

		return file;
	}

	private void addSlotImageToCanvas(Canvas canvas, Slot slot) {

		try {
			
		// get Image bitmap
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(slot.getPhotoPath());

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
		}
		catch (Exception exception) {
			int x =5;
		}
	}

}
