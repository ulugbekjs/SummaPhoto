package Generator;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import ActivationManager.DedicatedRequest;
import Common.ActualEventsBundle;
import Common.Photo;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class BlockCollageBuilder extends AbstractBuilder {
	
	private static final int BLOCK_TEMPLATES_NUM = 3;

	private static final String TAG = BlockCollageBuilder.class.getName();

	public BlockCollageBuilder(ActualEventsBundle bundle) {
		super(bundle);
	}
	
	public DedicatedRequest setTemplate() {
		return super.setTemplate(BLOCK_TEMPLATES_NUM);
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
			List<Integer> horizontals = template.getHorizontalSlots();
			List<Integer> verticals = template.getVerticalSlots();

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

		bmpBase = Bitmap.createBitmap(3264, 2448, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bmpBase);

		// draw images saved in Template onto canvas
		for (int slot = 0; slot < template.getNumberOfSlots(); slot ++) {
			try {
				addSlotImageToCanvasBySampling(bmpBase, canvas, template.getSlot(slot), 4);
			}
			catch (NullPointerException exception) {
				Log.e(TAG, "Empty Slot, cannot add to collage.");
			}
		}

		Photo collage = null;
		try {
			collage = saveCollageToFile(bmpBase); 
			clearProcessedPhotos(); // clear photos in container so they are not used again
		}
		catch (IOException exception) {
			Log.e(TAG, "Error when saving collage file");
			return null;
		}


		return collage;

	}





}
