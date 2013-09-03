package Generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jdom2.output.LineSeparator;

import com.adobe.xmp.impl.Utils;
import com.drew.imaging.ImageProcessingException;

import ActivationManager.DedicatedRequest;
import Bing.BingServices;
import Bing.Pushpin;
import Bing.StaticMap;
import Common.ActualEvent;
import Common.ActualEventsBundle;
import Common.Line;
import Common.Photo;
import Generator.LocatePicturesWithMap.SlotPushPinTuple;
import android.R.bool;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.Log;

public class MapCollageBuilder extends AbstractBuilder{
	
	List<Line> linesList = null;
	//static {@SuppressWarnings("unused")
	//byte[] dummy = new byte[36 * 1024 * 1024];
	//}
	
	private static final String TAG = MapCollageBuilder.class.getName();

	public MapCollageBuilder(ActualEventsBundle bundle) {
		super(bundle);
	}

	@Override
	public Photo buildCollage() {

		System.gc();
		Canvas canvas = null;
		Bitmap bmpBase = null;

		// TODO: remove. moved to comments since canvas size for map collage was changes
		// bmpBase = Bitmap.createBitmap(3264, 2448, Bitmap.Config.RGB_565);
		bmpBase = Bitmap.createBitmap(1469, 1102, Bitmap.Config.ARGB_8888);
		bmpBase.setHasAlpha(true);
		canvas = new Canvas(bmpBase);
			
		Slot slotToAddToCanvas;
		Bitmap bitmap = null;
		// draw images saved in Template onto canvas
		for (int slot = 0; slot < template.getNumberOfSlots(); slot ++) {
			try {
				slotToAddToCanvas = template.getSlot(slot);
				super.addSlotImageToCanvasBySampling(bitmap, canvas,slotToAddToCanvas, 4);
			}
			catch (NullPointerException exception) {
				// TODO: deal with error
				Log.e(TAG, "Could not add slot to canvas properly");
				int x = 5;
			}
		}
		
		// draw Bing map into output
		try {
			addSlotImageToCanvasBySampling(bitmap, canvas,((MapTemplate) template).getMapSlot(), 1);
		}
		catch (NullPointerException exception) {
			Log.e(TAG, "Could not add the map to canvas properly");
		}
		
		for (Line line : linesList) {
			// add lines
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(Color.rgb(62, 156, 250));
			//paint.setShader(new LinearGradient(0, 0, line.getLineXDelta(), line.getLineYDelta(), Color.YELLOW, Color.WHITE, android.graphics.Shader.TileMode.MIRROR));
			paint.setStrokeWidth(5f);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStyle(Style.FILL_AND_STROKE);
			paint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
//			paint.setAlpha(120);
			canvas.drawLine(line.getFromPoint().getX(), line.getFromPoint().getY(),
					line.getToPoint().getX(), line.getToPoint().getY(), paint);
		}

		//free bitmap
		//		bitmap.recycle();
		////		bitmap = null;
		//
		//		// add lines
		//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//		paint.setColor(android.graphics.Color.MAGENTA);
		//		paint.setStrokeWidth(3f);
		//		canvas.drawLine(642, 2080, 2448, 642, paint);
		
		drawFrame(canvas, bmpBase.getWidth(), bmpBase.getHeight());

		Photo collage;
		try {
			collage = saveCollage(bmpBase);
			clearProcessedPhotos(); // not to reuse same photos
		}
		catch (IOException exception) {
			Log.e(TAG, "Error when saving collage file");
			return null;
		}
		Log.d(TAG, "finished");
		return collage;
	}


	/**
	 * This method locate the different pictures in the relevant slots. Return true upon success. 
	 */
	public boolean populateTemplate() {

		// decide which pictures from the different events will be included in the collage
		List<Photo> horizontalPhotosList = new LinkedList<Photo>();
		getHorizontalPhotosForTemplate(horizontalPhotosList);
		List<Photo> verticalPhotosList = new LinkedList<Photo>();
		getVerticalPhotosForTemplate(verticalPhotosList);
		List<Photo> photosList = new LinkedList<Photo>();
		photosList.addAll(verticalPhotosList);
		photosList.addAll(horizontalPhotosList);
		
		
		// get the map data and image from Bing
		StaticMap mapFromDataSource = BingServices.getStaticMap(photosList, 
				((MapTemplate)template).getMapPixelWidth(), ((MapTemplate)template).getMapPixelHeight());
		if (mapFromDataSource == null)
		{
			Log.d(TAG, "Stop populate template, because error occured while trying to get map from BING");
			return false;
		}
		
		
		HashMap<PixelPoint, Pushpin> pixelPointsToPushPins = getAdjustedPixelPointPushPinDictionary(mapFromDataSource.getPushPins());
		HashMap<PixelPoint, Slot> pixelPointsToSlot = getPixelPointSlotDictionaryHashMap(template.slots);
		
		if (pixelPointsToPushPins.size() != pixelPointsToSlot.size())
		{
			Log.d(TAG, "Stop populate template, because number of slots is not equal to number of pushpins");
			return false;
		}
		
		
		
		LocatePicturesWithMap locatePicturesWithMap = new LocatePicturesWithMap(pixelPointsToSlot, pixelPointsToPushPins);
		// decide which picture will be populated in each slot 
		List<SlotPushPinTuple> tuples = locatePicturesWithMap.matchPicturesOnMapToPointOnFrame();
		updatePicturesOfSlots (tuples,photosList);
		((MapTemplate)template).setMap(mapFromDataSource);
		linesList = convertTupplesToLines(tuples);
		
		return true;
	}
	
	private Path getArrowHead(int tipX, int tipY, double angle) {
		Path path = new Path();
		path.moveTo(tipX, tipY);
        path.lineTo(tipX - 20, tipY +  60);
        path.lineTo(tipX, tipY + 50);
        path.lineTo(tipX + 20, tipY + 60);
        path.close();
        
        Matrix mMatrix = new Matrix();
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        mMatrix.postRotate((float) angle, 
                           (bounds.right + bounds.left)/2, 
                           (bounds.bottom + bounds.top)/2);
        path.transform(mMatrix);
		return path;
	}
	
	/**
	@Override
	public DedicatedRequest setTemplate() {
		// TODO Auto-generated method stub
		return null;
	}
	**/
	
	
	/**
	 * @param slots - array of slots of the template
	 * @return dictionary which its keys are pixelPoints of "connection" points of the slots, and values are the relvant slots
	 */
	private HashMap<PixelPoint, Slot> getPixelPointSlotDictionaryHashMap (Slot[] slots)
	{
		if (slots == null)
			return null;
		HashMap<PixelPoint, Slot> pixelPointSlotDictionary = new  HashMap<PixelPoint, Slot>();
		for (Integer i=0; i< slots.length; i++)
		{
			if (slots[i].hasConnectingLinePoint())
				pixelPointSlotDictionary.put(slots[i].getConnectingLinePoint(), slots[i]);
		}
		return pixelPointSlotDictionary;
	}
	
	/**
	 * @param pushPins - the list of pushPins on map retrieved from bing
	 * @return Dictionary which contains the actual pixel of the pushPin in the output collage as key, and the pushPin object as value 
	 */
	
	private HashMap<PixelPoint, Pushpin> getAdjustedPixelPointPushPinDictionary(List<Pushpin> pushPins)
	{
		if (pushPins == null)
			return null;
		Integer xInterval = ((MapTemplate)template).getMapSlot().getTopLeft().getX();
		Integer yInterval = ((MapTemplate)template).getMapSlot().getTopLeft().getY();
		HashMap<PixelPoint, Pushpin> adjustedPushPinsPixelPoints = new HashMap<PixelPoint, Pushpin>();
		
		
		PixelPoint tempPixelPoint;
		Pushpin adjustedPushpin;
		// the adjusted pixel point for each pushPin its is originalX + the top left X coordinate of the map (the same for Y coordinate)
		for (Pushpin pin: pushPins)
		{
			tempPixelPoint = new PixelPoint(xInterval + pin.getAnchor().getX(), yInterval + pin.getAnchor().getY());
			pin.setAnchor(tempPixelPoint);
			adjustedPushPinsPixelPoints.put(tempPixelPoint, pin);
		}
		return adjustedPushPinsPixelPoints ;
	}
	
	
	/**
	 * @param tuples - tuples of location PixelPoint and pushPin pixelPoint
	 * @param photosList - list of photos in the collage
	 * The method assign to each slot in the template its relevant picture
	 */
	private boolean updatePicturesOfSlots (List<SlotPushPinTuple> tuples, List<Photo> photosList)
	{ 
		if ((tuples == null) || (photosList == null))
			return false;
		for (SlotPushPinTuple tuple : tuples)
		{
			tuple.getSlot().assignToPhoto(tuple.getPushpin().getPhoto());
		}
		return true;
	}
	/**
	 * @param tuplesList list slot to pushPin pin tuples, which should represents which slot is connected to each tuple in the collage
	 * @return lines that should be added to the final collage
	 */
	private List<Line> convertTupplesToLines (List<SlotPushPinTuple> tuplesList)
	{
		if (tuplesList == null)
			return null;
		List<Line> lineList = new LinkedList<Line>();
		for (SlotPushPinTuple tuple : tuplesList)
		{
			if (tuple.getSlot().hasConnectingLinePoint())
			{
				lineList.add(new Line(tuple.getSlot().getConnectingLinePoint(), tuple.getPushpin().getAnchor()));
			}
		}
		return lineList;
	}
	
	
	
}
