package Generator;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import Common.ActualEventsBundle;
import Common.Photo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class BlockCollageBuilder extends AbstractBuilder {

	private static final String TAG = BlockCollageBuilder.class.getName();

	public BlockCollageBuilder(ActualEventsBundle bundle) {
		super(bundle);
	}

	@Override
	public boolean populateTemplate() {

		boolean successful = true;

		List<Photo> pickedHorizontals = new LinkedList<Photo>();
		List<Photo> pickedVerticals = new LinkedList<Photo>();

		// getting horizontal photos
		successful &= getHorizontalPhotosForTemplate(pickedHorizontals);
		// getting vertical photos
		successful &= getVerticalPhotosForTemplate(pickedVerticals);

		if (successful) {
			List<Integer> horizontals = new LinkedList<Integer>(template.getHorizontalSlots());
			List<Integer> verticals = new LinkedList<Integer>(template.getVerticalSlots());

			// populate horizontal slots
			for (int slot : horizontals) {
				if (!pickedHorizontals.isEmpty()) {
					template.getSlot(slot).assignToPhoto(pickedHorizontals.remove(0));
				}
				else {
					successful = false;
				}
			}
			
			// populate vertical slots
			for (int slot : verticals) {
				if (!pickedVerticals.isEmpty()) {
					template.getSlot(slot).assignToPhoto(pickedVerticals.remove(0));
				}
			}
		}

		return successful;
	}


	@Override
	public Photo buildCollage() {

		Canvas canvas = null;
		Bitmap bmpBase = null;

		bmpBase = Bitmap.createBitmap(3264, 2448, Bitmap.Config.RGB_565);
		canvas = new Canvas(bmpBase);

		//		// draw images saved in Template onto canvas
		//		for (int slot = 0; slot < template.getNumberOfSlots(); slot ++) {
		//			try {
		//				addSlotImageToCanvas(canvas, template.getSlot(slot));
		//			}
		//			catch (NullPointerException exception) {
		//				// TODO: deal with error
		//			}
		//		}

		Photo collage = null;
		try {
			collage = saveCollage(bmpBase); 
			clearProcessedPhotos(); // clear photos in container so they are not used again
		}
		catch (IOException exception) {
			Log.e(TAG, "Error when saving collage file");
			return null;
		}


		return collage;

	}





}
