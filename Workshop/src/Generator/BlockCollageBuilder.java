package Generator;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import ActivationManager.DedicatedRequest;
import Common.ActualEvent;
import Common.ActualEventsBundle;
import Common.Photo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class BlockCollageBuilder extends AbstractBuilder {

	private static final String TAG = BlockCollageBuilder.class.getName();
	BlockTemplate template = null;

	public BlockCollageBuilder(ActualEventsBundle bundle) {
		super(bundle);
	}

	@Override
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
			if (templates[i].getHorizontalSlots().size() <= bundle.horizontalCount() &&
					templates[i].getVerticalSlots().size() <= bundle.verticalCount()) { // template fits perfectly for bundle
				chosenTemplate = templates[i];
				break;
			}
			if (templateDiffs[i] < min) { 
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

	@Override
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
		Collections.shuffle(slotsToFill);

		while (!slotsToFill.isEmpty() && !queue.isEmpty()) {
			ActualEvent event = queue.remove();

			List<Photo> photosInEvent = (horizontalPhotos) ? event.horizontalPhotos() : event.verticalPhotos(); 

			if (!photosInEvent.isEmpty()) { // horizontal photos in event
				Random random = new Random(new Date().getTime());
				int rand = random.nextInt(photosInEvent.size()); 
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

	private Queue<ActualEvent> getQueue() {
		Queue<ActualEvent> queue = new LinkedList<ActualEvent>();
		for (ActualEvent event: bundle.getActualEvents()) {
			queue.add(event);
		}
		return queue;
	}

	@Override
	public Photo buildCollage() {

		Canvas canvas = null;
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

		Photo collage = null;
		try {
			collage = saveCollage(bmpBase); 
		}
		catch (IOException exception) {
			Log.e(TAG, "Error when saving collage file");
			// TODO: notify user about error in saving collage
			return null;
		}
		
		clearProcessPhotos(); // clear photos in container so they are not used again
		
		return collage;

	}





}
