package Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import ActivationManager.SmartModeService;
import PhotoListener.PhotoListenerThread;
import android.os.Environment;
import android.provider.MediaStore.Files;

import com.drew.imaging.ImageProcessingException;
import com.example.aworkshop.SettingsActivity;

public class Tester {

	public static void insertFilesToObservedDir() {
		
		File ROOT = new File(Environment.getExternalStorageDirectory(), "DCIM");
		String  PHOTO_DIR = ROOT + File.separator + "Watched" + File.separator;

		PhotoListenerThread observer = new PhotoListenerThread(PHOTO_DIR); // observer over the gallery directory
		observer.startWatching();
		
		SettingsActivity.MODE = 1;
		SettingsActivity.COLLAGE_TYPE = 2;

		File dest = new File(SettingsActivity.ROOT, "Watched");
		if (!dest.exists()) {
			dest.mkdirs();
		}

		File source = new File(SettingsActivity.ROOT, "Tests");

		File[] files = source.listFiles();
		List<Photo> photos = new LinkedList<Photo>();
		for (File file : files) {
			try {
				photos.add(Utils.createPhotoFromFile(file.getAbsolutePath()));
			} catch (ImageProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Collections.sort(photos, new Comparator<Photo>() {

			@Override
			public int compare(Photo lhs, Photo rhs) {
				return lhs.compareTo(rhs);
			}
		});

		List<File> sortedFileList = new LinkedList<File>();
		for (Photo photo : photos) {
			sortedFileList.add(new File(photo.getFilePath()));
		}

		//photos added by the order that they were taken in
		for (File file : sortedFileList) {
			try {
				copyFile(file, new File(dest, file.getName()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	private static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!sourceFile.exists()) {
			return;
		}
		if (destFile.exists()) {
			destFile.delete();
		}
		destFile.createNewFile();

		FileChannel source = null;
		FileChannel destination = null;
		source = new FileInputStream(sourceFile).getChannel();
		destination = new FileOutputStream(destFile).getChannel();
		if (destination != null && source != null) {
			destination.transferFrom(source, 0, source.size());
		}
		if (source != null) {
			source.close();
		}
		if (destination != null) {
			destination.close();
		}
	}
}
