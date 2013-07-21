package PhotoListener;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.junit.Test;

import Common.Photo;
import Common.Point;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;


public class ListenerTestCases {

	@Test
	public void extractionTest() {
		PhotoListenerThread t = new PhotoListenerThread();
		t.createPhotoFromFile(null);
	}

}
