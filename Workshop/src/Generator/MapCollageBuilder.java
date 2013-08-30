package Generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import Common.ActualEventsBundle;
import Common.Photo;
import Common.PhotoContainer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

public class MapCollageBuilder extends AbstractBuilder{
	
	MapTemplate template = null;
	private static final String TAG = MapCollageBuilder.class.getName();

	public MapCollageBuilder(ActualEventsBundle bundle) {
		super(bundle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Photo buildCollage() {

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

		// draw Bing map into output
		try {
			addSlotImageToCanvas(canvas, template.getMapSlot());
		}
		catch (NullPointerException exception) {
			//TODO: deal with error
		}

		// add lines
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(android.graphics.Color.MAGENTA);
		paint.setStrokeWidth(3f);
		canvas.drawLine(642, 2080, 2448, 642, paint);

		Photo collage;
		try {
			collage = saveCollage(bmpBase);
		}
		catch (IOException exception) {
			Log.e(TAG, "Error when saving collage file");
			// TODO: notify user about error in saving collage
			return null;
		}

		clearProcessPhotos(); // not to reuse same photos
		
		return collage;
	}

	@Override
	public boolean populateTemplate() {
		// TODO Auto-generated method stub
		return false;
	}

	
}
